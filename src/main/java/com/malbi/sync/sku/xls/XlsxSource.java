package com.malbi.sync.sku.xls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.malbi.sync.sku.model.Changes;
import com.malbi.sync.sku.model.DBSKUGroup;
import com.malbi.sync.sku.model.DbRowData;
import com.malbi.sync.sku.model.SKUGroupChanges;
import com.malbi.sync.sku.model.XlsRowData;

public class XlsxSource {

	public XlsxSource(Path p) {
		this.filePath = p;
		try {
			initData();
		} catch (IOException e) {

		}
	}

	// 02.10.2015, Andrew Duplik.
	// Will add default constructor to split object creation and validation.
	public XlsxSource() {

	}

	public List<Changes> getGroupUpdates(Map<Integer, String> dbSkuGropMap) {
		Map<Integer, Changes> changes = new HashMap<Integer, Changes>();
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

		return new ArrayList<Changes>(changes.values());
	}

	public List<SKUGroupChanges> getDBGroupUpdates(Map<Integer, String> dbSkuGropMap) {

		Map<Integer, SKUGroupChanges> groupChanges = new HashMap<Integer, SKUGroupChanges>();
		// SKUService service = new SKUService();
		// List<DBSKUGroup> dbGroupsList = service.getDBSKUgroups();

		this.rows.stream().forEach(t -> {
			Integer key = t.getSkuGroupCode();
			if (!dbSkuGropMap.containsKey(key)) {
				// new sku group
				int groupCode = Integer.getInteger(t.getSkuGroup());
				SKUGroupChanges change = new SKUGroupChanges(key, new DBSKUGroup(),
						new DBSKUGroup(groupCode, dbSkuGropMap.get(groupCode)));

				groupChanges.put(key, change);
			} else {
				// renames sku group
				String dbSkuGroupName = dbSkuGropMap.get(key);
				String xlsSkuGroupName = t.getSkuGroup();
				if (!dbSkuGroupName.equals(xlsSkuGroupName)) {
					SKUGroupChanges change = new SKUGroupChanges(key, new DBSKUGroup(key, dbSkuGroupName),
							new DBSKUGroup(key, xlsSkuGroupName));
					groupChanges.put(key, change);
				}
			}

		});

		return new ArrayList<SKUGroupChanges>(groupChanges.values());

	}

	public List<Changes> getSkuUpdates(Map<Integer, DbRowData> dbSkuMap) {
		List<Changes> changes = new ArrayList<Changes>();
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

	public void updateXlsSource() throws FileNotFoundException, IOException {
		Path parent = this.XlsFile.toPath().getParent();
		// first we need to backup file?
		String backUpFile = new SimpleDateFormat("yyyy-MM-dd-(HH-mm-ss)").format(System.currentTimeMillis()) + ".xls";
		Path backUpPath = parent.resolve(Paths.get("back_up", backUpFile));

		backUpPath.getParent().toFile().mkdirs();
		// try {
		Files.copy(this.XlsFile.toPath(), backUpPath);

		POIFSFileSystem stream;
		HSSFWorkbook book;
		HSSFSheet sheet;
		// stream = new POIFSFileSystem(new
		// FileInputStream(this.filePath.toFile()));

		// There is a more modern example
		// Workbook destBook = WorkbookFactory.create(srcFile);
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

		// File result = this.filePath.toFile();
		// FileOutputStream out = new FileOutputStream(result);
		// book.close(); // or it causes Position 4498432 past the end of the
		// file
		FileOutputStream out = new FileOutputStream(this.XlsFile);
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
		String defaultMsg = "Ошибка проверки XLS-файла.\n";
		String error = defaultMsg;

		// NOT FILLED ROWS
		// ///////////////////////////////////////////////////////////////
		Set<Integer> notFilledRows = new HashSet<Integer>();
		for (XlsRowData r : rows) {
			if (r.getSkuCode() == 0 || r.getSkuName().equals("") || r.getSkuGroup().equals("")
					|| r.getSkuGroupCode() == 0 || r.getBusiness().equals("") || r.getSubGroup().equals("")
					|| r.getBusinessSort() == 0 || r.getSubGroupSort() == 0 || r.getGroupSort() == 0) {
				notFilledRows.add(r.getRowNo());
			}
		}

		if (notFilledRows.size() > 0) {
			error += "Эти строки заполнены не полностью:\n";
			for (Integer i : notFilledRows) {
				error += String.valueOf(i) + "; ";
			}
			error += "\n";
		}

		// DUBLE ID
		// ///////////////////////////////////////////////////////////////
		Set<Integer> allId = new HashSet<Integer>();
		Set<Integer> dubId = new HashSet<Integer>();

		for (XlsRowData r : rows) {
			if (!allId.add(r.getSkuCode())) {
				dubId.add(r.getSkuCode());
			}
		}

		if (dubId.size() > 0) {
			error += "Следующие идентификаторы SKU имеют дубли:\n";
			for (Integer i : dubId) {
				error += String.valueOf(i) + "; ";
			}
			error += "\n";
		}

		// DIFFERENT PROPERTIES FOR THE SAME GROUP
		// ID///////////////////////////////
		Map<Integer, XlsRowData> allGroupId = new HashMap<Integer, XlsRowData>();
		Set<Integer> errorGroupId = new HashSet<Integer>();
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

		if (errorGroupId.size() > 0) {
			error += "Эти идентификаторы групп SKU имеют различные свойства:\n";
			for (Integer i : errorGroupId) {
				error += String.valueOf(i) + "; ";
			}
			error += "\n";
		}

		if (error.equals(defaultMsg)) {
			// System.out.println("XlsFile Internal validationd has finished
			// successfully.");
			return true;
		} else {
			// System.out.println(error + MainApp.END_MSG);
			// System.exit(1);
			// let's return custom error log to calling class.
			this.validationErrorLog = error;
			return false;
		}

	}

	public void initData() throws FileNotFoundException, IOException {
		POIFSFileSystem stream;
		HSSFWorkbook book;
		HSSFSheet sheet;
		XlsRowData row;

		// 02.10.2015, Andrei Duplik.
		// Documentation says that it is prefferable to create this object from
		// file.
		// stream = new POIFSFileSystem(new
		// FileInputStream(this.filePath.toFile()));
		stream = new POIFSFileSystem(this.XlsFile);

		book = new HSSFWorkbook(stream);
		sheet = book.getSheet("base");
		int rowNo = 1;
		do {
			row = new XlsRowData(rowNo + 1, (int) getCell(rowNo, 0, sheet).getNumericCellValue(),
					getCell(rowNo, 1, sheet).getRichStringCellValue().toString(),
					getCell(rowNo, 2, sheet).getRichStringCellValue().toString(),
					(int) getCell(rowNo, 3, sheet).getNumericCellValue(),
					getCell(rowNo, 4, sheet).getRichStringCellValue().toString(),
					getCell(rowNo, 5, sheet).getRichStringCellValue().toString(),
					(int) getCell(rowNo, 6, sheet).getNumericCellValue(),
					(int) getCell(rowNo, 7, sheet).getNumericCellValue(),
					(int) getCell(rowNo, 8, sheet).getNumericCellValue(),
					(int) getCell(rowNo, 9, sheet).getNumericCellValue());
			rowNo++;
			if (!row.isEmpty()) {
				rows.add(row);
			}

		} while (!row.isEmpty());

		book.close();
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

	private List<XlsRowData> rows = new LinkedList<XlsRowData>();

	public List<XlsRowData> getRows() {
		return rows;
	}

	public void setRows(List<XlsRowData> rows) {
		this.rows = rows;
	}

	private Path filePath;

	private File XlsFile;

	public File getXlsFile() {
		return XlsFile;
	}

	public void setXlsFile(File xlsFile) {
		XlsFile = xlsFile;
	}

	private String validationErrorLog;

	public String getValidationErrorLog() {
		return validationErrorLog;
	}

	public void setValidationErrorLog(String validationErrorLog) {
		this.validationErrorLog = validationErrorLog;
	}
}