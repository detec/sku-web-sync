package com.malbi.sync.sku.xls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.malbi.sync.sku.converter.Exception2String;
import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.DbRowData;
import com.malbi.sync.sku.model.SKUGroupChanges;
import com.malbi.sync.sku.model.XlsRowData;
import com.malbi.sync.sku.service.SKUService;

@Named
@SessionScoped
public class XlsxSource implements Serializable {

	private static final long serialVersionUID = 1936115774792361359L;

	private static final String CONSTANT_DELIMITER = ";";

	private static final String CONSTANT_NEW_LINE = "\n";

	@Inject
	private SKUService service;

	private Path filePath;

	private File xlsFile;

	private List<XlsRowData> rows = new LinkedList<>();

	private String exceptionString = "";

	private String validationErrorLog = "";

	/**
	 * Will add default constructor to split object creation and validation.
	 */
	public XlsxSource() {

	}

	public SKUService getService() {
		return service;
	}

	public void setService(SKUService service) {
		this.service = service;
	}

	public List<Changes> getGroupUpdates() {

		StringBuilder log = new StringBuilder();
		Map<Integer, String> dbSkuGropMap = service.getSkuGroupMap();
		appendLogAtRefresh(service, log); // check if we got errors.

		Map<Integer, Changes> changes = new HashMap<>();
		Integer key;
		String dbSkuGroupName;
		String xlsSkuGroupName;
		for (XlsRowData xls : this.rows) {
			key = xls.getSkuGroupCode();
			if (!dbSkuGropMap.containsKey(key)) {
				// new sku group
				changes.put(key, new Changes(key, null, xls.getSkuGroup()));
			} else {
				// renames sku group
				dbSkuGroupName = dbSkuGropMap.get(key);
				xlsSkuGroupName = xls.getSkuGroup();
				if (!dbSkuGroupName.equals(xlsSkuGroupName)) {
					changes.put(key, new Changes(key, dbSkuGroupName, xlsSkuGroupName));
				}
			}
		}

		return new ArrayList<>(changes.values());
	}

	/**
	 * This is the original Bondarenko's method
	 *
	 * @param dbSkuMap
	 * @return
	 */
	public List<Changes> getSkuUpdates(Map<Integer, DbRowData> dbSkuMap) {
		List<Changes> changes = new ArrayList<>();
		Integer key;
		int dbParentId;
		int xlsParentId;
		for (XlsRowData xls : this.rows) {
			key = xls.getSkuCode();
			if (!dbSkuMap.containsKey(key)) {
				// new sku
				changes.add(new Changes(key, null, String.valueOf(xls.getSkuGroupCode())));
			} else {
				// moved to new group
				dbParentId = dbSkuMap.get(key).getParentId();
				xlsParentId = xls.getSkuGroupCode();
				if (dbParentId != xlsParentId) {
					changes.add(new Changes(key, String.valueOf(dbParentId), String.valueOf(xlsParentId)));
				}
				// all entries that are left in dbSkuMap -> are deleted from
				// xlsSource
				dbSkuMap.remove(key);
			}
		}
		// deleted values from xls source
		for (DbRowData d : dbSkuMap.values()) {
			changes.add(new Changes(d.getNodeId(), String.valueOf(d.getParentId()), null));
		}

		return changes;
	}

	/**
	 * This method should return not groups' codes but DBGroup objects with
	 * name. Andrei Duplik wrote after getSkuUpdates
	 *
	 * @return
	 */
	public List<SKUGroupChanges> getSKUUpdatesDBGroups() {

		StringBuilder log = new StringBuilder();

		// so it returns the position of sku in the hierarchy.
		Map<Integer, DbRowData> dbSkuMap = service.getSkuHierarchyMap();
		appendLogAtRefresh(service, log);

		// makes query to "select group_id" + " ,group_name" + " from
		// xx_rs_sku_groups"
		Map<Integer, String> mapDBGroups = service.getSkuGroupMap();
		appendLogAtRefresh(service, log);

		List<SKUGroupChanges> changes = new ArrayList<>();

		this.rows.stream().forEach(t -> processRow(t, dbSkuMap, mapDBGroups, changes));

		// deleted values from xls source
		dbSkuMap.values().stream().forEach(t -> {
			SKUGroupChanges change = new SKUGroupChanges(t.getNodeId(),
					new DBSKUGroup(t.getParentId(), mapDBGroups.get(t.getParentId())), new DBSKUGroup());
			changes.add(change);
		});

		// put errors from db to current object exception string.
		if (!log.toString().isEmpty()) {
			this.exceptionString = log.toString();
		}
		return changes;

	}

	private void processRow(XlsRowData t, Map<Integer, DbRowData> dbSkuMap, Map<Integer, String> mapDBGroups,
			List<SKUGroupChanges> changes) {
		Integer key = t.getSkuCode();

		// check if xx_rs_sku_hierarchy contains node_id equal to sku id in
		// xls row.
		if (!dbSkuMap.containsKey(key)) {

			// new sku, according to Bondarenko.
			Integer groupCode = t.getSkuGroupCode();

			// get group name from common groups ref xx_rs_sku_groups
			String pName = mapDBGroups.get(groupCode);

			// new DBSKUGroup() equals to null in Bondarenko's version
			SKUGroupChanges change = new SKUGroupChanges(key, new DBSKUGroup(),
					new DBSKUGroup(groupCode.intValue(), pName));

			changes.add(change);
		} else {
			// moved to new group
			int dbParentId = dbSkuMap.get(key).getParentId();
			int xlsParentId = t.getSkuGroupCode();
			if (dbParentId != xlsParentId) {
				SKUGroupChanges change = new SKUGroupChanges(key,
						new DBSKUGroup(dbParentId, mapDBGroups.get(dbParentId)),
						new DBSKUGroup(xlsParentId, mapDBGroups.get(xlsParentId)));
				changes.add(change);
			}
			// all entries that are left in dbSkuMap -> are deleted from
			// xlsSource
			dbSkuMap.remove(key);

		}
	}

	public void updateXlsSource() throws IOException {
		Path parent = this.xlsFile.toPath().getParent();
		// first we need to backup file?
		String backUpFile = new SimpleDateFormat("yyyy-MM-dd-(HH-mm-ss)").format(System.currentTimeMillis()) + ".xls";
		Path backUpPath = parent.resolve(Paths.get("back_up", backUpFile));

		backUpPath.getParent().toFile().mkdirs();
		Files.copy(this.xlsFile.toPath(), backUpPath);

		POIFSFileSystem stream;
		HSSFWorkbook book;
		HSSFSheet sheet;

		File destFile = new File(backUpPath.toString());

		stream = new POIFSFileSystem(destFile);
		book = new HSSFWorkbook(stream);
		sheet = book.getSheet("base");
		int rowNo = 1;
		int startOfNotNeededRows = rowNo + this.rows.size();
		int lastRowNumber = sheet.getLastRowNum();
		for (int i = 0, s = this.rows.size(); i < s; i++) {
			getCell(rowNo + i, 0, sheet).setCellValue(rows.get(i).getSkuCode());
			getCell(rowNo + i, 1, sheet).setCellValue(rows.get(i).getSkuName());
			getCell(rowNo + i, 2, sheet).setCellValue(rows.get(i).getSkuGroup());
			getCell(rowNo + i, 3, sheet).setCellValue(rows.get(i).getSkuGroupCode());
			getCell(rowNo + i, 4, sheet).setCellValue(rows.get(i).getBusiness());
			getCell(rowNo + i, 5, sheet).setCellValue(rows.get(i).getSubGroup());
			getCell(rowNo + i, 6, sheet).setCellValue(rows.get(i).getPrimaryGroup());
			getCell(rowNo + i, 7, sheet).setCellValue(rows.get(i).getBusinessSort());
			getCell(rowNo + i, 8, sheet).setCellValue(rows.get(i).getSubGroupSort());
			getCell(rowNo + i, 9, sheet).setCellValue(rows.get(i).getGroupSort());
		}
		for (int i = lastRowNumber; i >= startOfNotNeededRows; i--) {
			sheet.removeRow(sheet.getRow(i));
		}

		FileOutputStream out = new FileOutputStream(this.xlsFile);
		book.write(out);
		out.close();
		book.close();
		// finally there is no need to do anything as we read data from backup
		// file.

	}

	public int getArrayIdOfXlsRowDataBySkuCode(int skuCode) {
		for (int i = 0, s = rows.size(); i < s; i++) {
			if (skuCode == rows.get(i).getSkuCode()) {
				return i;
			}
		}

		return -1;
	}

	public boolean validateInternal() {
		StringBuilder sb = new StringBuilder();

		String defaultMsg = "Ошибка проверки XLS-файла.\n";
		// String error = defaultMsg;
		sb.append(defaultMsg);

		// NOT FILLED ROWS
		// ///////////////////////////////////////////////////////////////
		Set<Integer> notFilledRows = new HashSet<>();
		for (XlsRowData r : rows) {
			if (r.getSkuCode() == 0 || r.getSkuName().isEmpty() || r.getSkuGroup().isEmpty() || r.getSkuGroupCode() == 0
					|| r.getBusiness().isEmpty() || r.getSubGroup().isEmpty() || r.getBusinessSort() == 0
					|| r.getSubGroupSort() == 0 || r.getGroupSort() == 0) {
				notFilledRows.add(r.getRowNo());
			}
		}

		if (!notFilledRows.isEmpty()) {

			sb.append("Эти строки заполнены не полностью:\n");

			String joined = notFilledRows.stream().map(String::valueOf).collect(Collectors.joining(CONSTANT_DELIMITER));
			sb.append(joined);
			sb.append(CONSTANT_NEW_LINE);
		}

		// Duplicate IDs
		// ///////////////////////////////////////////////////////////////
		Set<Integer> allId = new HashSet<>();
		Set<Integer> dubId = new HashSet<>();

		for (XlsRowData r : rows) {
			if (!allId.add(r.getSkuCode())) {
				dubId.add(r.getSkuCode());
			}
		}

		if (!dubId.isEmpty()) {

			sb.append("Следующие идентификаторы SKU имеют дубли:\n");

			String joined = dubId.stream().map(String::valueOf).collect(Collectors.joining(CONSTANT_DELIMITER));
			sb.append(joined);
			sb.append(CONSTANT_NEW_LINE);
		}

		// DIFFERENT PROPERTIES FOR THE SAME GROUP
		// ID///////////////////////////////
		Map<Integer, XlsRowData> allGroupId = new HashMap<>();
		Set<Integer> errorGroupId = new HashSet<>();
		XlsRowData rData;
		for (XlsRowData r : rows) {
			if (allGroupId.containsKey(r.getSkuGroupCode())) {
				rData = allGroupId.get(r.getSkuGroupCode());
				if (!r.getSkuGroup().equals(rData.getSkuGroup()) || !r.getBusiness().equals(rData.getBusiness())
						|| !r.getSubGroup().equals(rData.getSubGroup())
						|| r.getBusinessSort() != rData.getBusinessSort()
						|| r.getSubGroupSort() != rData.getSubGroupSort() || r.getGroupSort() != rData.getGroupSort()) {
					errorGroupId.add(r.getSkuGroupCode());
				}
			} else {
				allGroupId.put(r.getSkuGroupCode(), r);
			}
		}

		if (!errorGroupId.isEmpty()) {

			String joined = errorGroupId.stream().map(String::valueOf).collect(Collectors.joining(CONSTANT_DELIMITER));
			sb.append(joined);
			sb.append(CONSTANT_NEW_LINE);

		}

		if (sb.toString().equals(defaultMsg)) {
			return true;
		} else {

			this.validationErrorLog = sb.toString();
			return false;
		}

	}

	public void initData() throws Exception {
		POIFSFileSystem stream;
		HSSFWorkbook book;
		HSSFSheet sheet;
		XlsRowData row;

		stream = new POIFSFileSystem(this.xlsFile);

		book = new HSSFWorkbook(stream);
		sheet = book.getSheet("base");
		// here can be no sheet with such name
		if (sheet == null) {
			book.close();
			throw new IllegalStateException("Не найден лист base!");
		}

		int rowNo = 1;
		do {
			int currentRow = rowNo + 1;

			int col0 = readNumericCell(rowNo, 0, book, currentRow, sheet);

			int col3 = readNumericCell(rowNo, 3, book, currentRow, sheet);

			int col6 = readNumericCell(rowNo, 6, book, currentRow, sheet);

			int col7 = readNumericCell(rowNo, 7, book, currentRow, sheet);

			int col8 = readNumericCell(rowNo, 8, book, currentRow, sheet);

			int col9 = readNumericCell(rowNo, 9, book, currentRow, sheet);

			row = new XlsRowData(rowNo + 1, col0, getCell(rowNo, 1, sheet).getRichStringCellValue().toString(),
					getCell(rowNo, 2, sheet).getRichStringCellValue().toString(), col3,
					getCell(rowNo, 4, sheet).getRichStringCellValue().toString(),
					getCell(rowNo, 5, sheet).getRichStringCellValue().toString(), col6, col7, col8, col9);
			rowNo++;
			if (!row.isEmpty()) {
				rows.add(row);
			}

		} while (!row.isEmpty());

		book.close();
	}

	private int readNumericCell(int rowNo, int colPosition, HSSFWorkbook book, int currentRow, HSSFSheet sheet)
			throws Exception {

		int col0 = 0;
		try {
			col0 = (int) getCell(rowNo, colPosition, sheet).getNumericCellValue();
		} catch (Exception e) {
			book.close();

			throw new Exception("Не получилось получить числовое значение из строки " + Integer.toString(currentRow)
					+ " и колонки " + Integer.toString(colPosition + 1) + "\n" + Exception2String.printStackTrace(e));
		}
		return col0;
	}

	private HSSFCell getCell(int rowIndex, int cellIndex, HSSFSheet sheet) {
		HSSFRow row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
		}

		HSSFCell cell = row.getCell(cellIndex);
		if (cell == null) {
			cell = row.createCell(cellIndex);
		}

		return cell;
	}

	public Path getFilePath() {
		return filePath;
	}

	public void setFilePath(Path filePath) {
		this.filePath = filePath;
	}

	public List<XlsRowData> getRows() {
		return rows;
	}

	public void setRows(List<XlsRowData> rows) {
		this.rows = rows;
	}

	public File getXlsFile() {
		return xlsFile;
	}

	public void setXlsFile(File xlsFile) {
		this.xlsFile = xlsFile;
	}

	public String getValidationErrorLog() {
		return validationErrorLog;
	}

	public void setValidationErrorLog(String validationErrorLog) {
		this.validationErrorLog = validationErrorLog;
	}

	public String getExceptionString() {
		return exceptionString;
	}

	public void setExceptionString(String exceptionString) {
		this.exceptionString = exceptionString;
	}

	public void appendLogAtRefresh(SKUService service, StringBuilder log) {
		if (!log.toString().isEmpty()) {
			log.append(service.getErrorLog());
		}
	}

}