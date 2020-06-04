package com.barclays.automation.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.barclays.automation.entity.LinuxJavaDataSheet;
import com.barclays.automation.util.ExcelUtil;

@Service
public class DataReportService {

	private final ExcelUtil excelUtil;
	@Value("${excel.filepath}")
	private String filepath;
	@Value("${date.format}")
	private String dateFormat;
	public static final Integer COLUMN_COMMENT_NUM = 27;
	public static final Integer COLUMN_PROPOSED_DATE_NUM = 28;

	public DataReportService(ExcelUtil excelUtil) {
		this.excelUtil = excelUtil;
	}

	public List<LinuxJavaDataSheet> getLinuxJavaDataReport() throws Exception {

		FileInputStream file = new FileInputStream(filepath);

		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);
		Supplier<Stream<Row>> rowStreamSupplier = excelUtil.getRowStreamSupplier(sheet);

		return rowStreamSupplier.get()
				.skip(1) // as it is a header.
				.map(row -> {
					return populateLinuxJavaDataSheetByRow(row); 
				})
				.collect(Collectors.toList());
	}

	private LinuxJavaDataSheet populateLinuxJavaDataSheetByRow(Row row) {
		LinuxJavaDataSheet dataReportObj = new LinuxJavaDataSheet();
		dataReportObj.setSrNo(getIntegerValue(row.getCell(0)));
		dataReportObj.setPlatform(getStringValue(row.getCell(1)));
		dataReportObj.setServerName(getStringValue(row.getCell(2)));
		dataReportObj.setEnv(getStringValue(row.getCell(3)));
		dataReportObj.setTc(getStringValue(row.getCell(4)));
		dataReportObj.setService(getStringValue(row.getCell(5)));
		dataReportObj.setItsi(getStringValue(row.getCell(6)));
		dataReportObj.setRtbManager(getStringValue(row.getCell(7)));
		dataReportObj.setRtbLead(getStringValue(row.getCell(8)));
		dataReportObj.setIsPrimary(getBooleanValue(row.getCell(9)));
		dataReportObj.setJavaLocation(getStringValue(row.getCell(10)));
		dataReportObj.setJavaClass(getStringValue(row.getCell(11)));
		dataReportObj.setFileVersion(getStringValue(row.getCell(12)));
		dataReportObj.setJavaVersion(getIntegerValue(row.getCell(13)));
		dataReportObj.setJavaType(getStringValue(row.getCell(14)));
		dataReportObj.setPbtCiName(getStringValue(row.getCell(15)));
		dataReportObj.setCommandLastExecuted(getDateValue(row.getCell(16)));
		dataReportObj.setDormancy(getStringValue(row.getCell(17)));
		dataReportObj.setLowCritCount(getIntegerValue(row.getCell(18)));
		dataReportObj.setMedCritCount(getIntegerValue(row.getCell(19)));
		dataReportObj.setHighCritCount(getIntegerValue(row.getCell(20)));
		dataReportObj.setUtilityServer(getStringValue(row.getCell(21)));
		dataReportObj.setUtilityName(getStringValue(row.getCell(22)));
		dataReportObj.setVendor(getStringValue(row.getCell(23)));
		dataReportObj.setEmbeddedType(getStringValue(row.getCell(24)));
		dataReportObj.setJavaClass2(getStringValue(row.getCell(25)));
		dataReportObj.setJavaRelease(getIntegerValue(row.getCell(26)));
		dataReportObj.setJavaBuild(getIntegerValue(row.getCell(27)));
		dataReportObj.setSuspectedLatestJavaVersion(getStringValue(row.getCell(28)));
		dataReportObj.setComments(getStringValue(row.getCell(29)));
		dataReportObj.setProposedDate(getDateValue(row.getCell(30)));
		return dataReportObj;
	}

	private String getStringValue(Cell cell) {
		return (cell != null && cell.getCellType() == CellType.STRING) ?  cell.getStringCellValue() : "";
	}

	private Date getDateValue(Cell cell) {
		return cell != null ?  cell.getDateCellValue() : null;
	}

	private Integer getIntegerValue(Cell cell) {
		return (cell != null && cell.getCellType() == CellType.NUMERIC) ?  (int)cell.getNumericCellValue() : null;
	}

	private Boolean getBooleanValue(Cell cell) {
		return (cell != null && cell.getCellType() == CellType.BOOLEAN) ?  cell.getBooleanCellValue() : Boolean.FALSE;
	}

	/*public LinuxJavaDataSheet updateDataReport(String serverName, String comment, Date proposedDate) throws Exception {
		FileInputStream file = new FileInputStream(filepath);

		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);

		//Retrieve the row and check for null
		XSSFRow sheetrow = sheet.getRow(findRow(sheet, serverName));
		if(sheetrow == null){
			throw new Exception("Server not found with name : " + serverName);
		}

		//Update the value of cell
		
		String commentExisting=getStringValue(sheetrow.getCell(26));
		commentExisting=commentExisting +","+ comment;
		Cell commentCell = getCellFromRowByNum(sheetrow, COLUMN_COMMENT_NUM);
		commentCell.setCellValue(commentExisting);
		Cell proposedDateCell = getCellFromRowByNum(sheetrow, COLUMN_PROPOSED_DATE_NUM);
		proposedDateCell.setCellValue(proposedDate);
		modifyCellTypeAsDate(workbook, proposedDateCell);

		file.close();

		//Update Excel.
		FileOutputStream outFile =new FileOutputStream(new File(filepath));
		workbook.write(outFile);
		outFile.close();

		return populateLinuxJavaDataSheetByRow(sheetrow);
	}*/

	/*private void modifyCellTypeAsDate(XSSFWorkbook workbook, Cell cell) {
		CellStyle cellStyle = workbook.createCellStyle();
		CreationHelper creationHelper = workbook.getCreationHelper();
		cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(dateFormat));
		cell.setCellStyle(cellStyle);
	}

	private Cell getCellFromRowByNum(XSSFRow sheetrow, int columnNum) {
		Cell cell = sheetrow.getCell(columnNum);
		if(cell == null){
			cell = sheetrow.createCell(columnNum);
		}
		return cell;
	}

	private static int findRow(XSSFSheet sheet, String cellContent) {
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getCellType() == CellType.STRING) {
					if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
						return row.getRowNum();  
					}
				}
			}
		}               
		return -1;
	}
*/
}
