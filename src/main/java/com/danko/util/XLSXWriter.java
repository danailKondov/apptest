package com.danko.util;

import com.danko.model.Answer;
import com.danko.model.Question;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class XLSXWriter {

    private static final int ID_COLUMN_INDEX = 0;
    private static final int QUESTION_TEXT_COLUMN_INDEX = 1;
    private static final int ANSWER_OPTIONS_COLUMN_INDEX = 2;
    private static final int CANDIDATE_ANSWER_COLUMN_INDEX = 3;
    private static final int RIGHT_ANSWER_COLUMN_INDEX = 4;
    private static final int RESULT_COLUMN_INDEX = 5;
    private static final int POINTS_COLUMN_INDEX = 6;
    private static final short MAIN_ROW_HEIGHT = (short) (40 * 20);
    private static final short REGULAR_ROW_HEIGHT = (short) (50 * 20);
    private static final String FILE_NAME_PATTERN = "test_result_%s_%s.xlsx";


    private String firstName;
    private String lastName;
    private XSSFWorkbook workbook;
    private int rows;

    public void writeResults(List<Question> questions, String firstName, String lastName, double result) throws IOException {
        this.firstName = firstName;
        this.lastName = lastName;
        workbook = new XSSFWorkbook();

        XSSFSheet sheet = createFormattedSheet();
        addNameTimeRows(sheet);
        createTitleRow(sheet);

        for (Question question : questions) {

            XSSFRow row = sheet.createRow(rows++);
            row.setHeight(REGULAR_ROW_HEIGHT);

            XSSFCellStyle cellWrapTextStyle = getWrapStyle();
            XSSFCellStyle cellBottomBorderAndWrapTextStyle = getBorderAndWrapTextlStyle();
            XSSFCellStyle cellBottomBorderStyle = getBottomBorderStyle();

            // id
            XSSFCell idCell = row.createCell(ID_COLUMN_INDEX, CellType.NUMERIC);
            idCell.setCellValue(question.getId());
            idCell.setCellStyle(question.getAnswers().size() < 2 ? cellBottomBorderAndWrapTextStyle : cellWrapTextStyle);

            // вопрос
            XSSFCell questionTextCell = row.createCell(QUESTION_TEXT_COLUMN_INDEX, CellType.STRING);
            questionTextCell.setCellValue(question.getQuestionText());
            questionTextCell.setCellStyle(question.getAnswers().size() < 2 ? cellBottomBorderAndWrapTextStyle : cellWrapTextStyle);

            // ответы
            List<Answer> answers = question.getAnswers();
            if (answers != null) {
                if (answers.size() == 0) {
                    XSSFCell answerTextCell = row.createCell(ANSWER_OPTIONS_COLUMN_INDEX, CellType.STRING);
                    answerTextCell.setCellValue("Нет ответа");
                    answerTextCell.setCellStyle(cellBottomBorderAndWrapTextStyle);

                    XSSFCell candidateAnswerCell = row.createCell(CANDIDATE_ANSWER_COLUMN_INDEX, CellType.STRING);
                    candidateAnswerCell.setCellStyle(cellBottomBorderStyle);

                    XSSFCell rightAnswerCell = row.createCell(RIGHT_ANSWER_COLUMN_INDEX, CellType.STRING);
                    rightAnswerCell.setCellStyle(cellBottomBorderStyle);

                    XSSFCell answerResultCell = row.createCell(RESULT_COLUMN_INDEX, CellType.STRING);
                    answerResultCell.setCellStyle(cellBottomBorderStyle);

                    XSSFCell answerPointsCell = row.createCell(POINTS_COLUMN_INDEX, CellType.STRING);
                    answerPointsCell.setCellValue(String.format("%.2f", question.getScore()));
                    answerPointsCell.setCellStyle(cellBottomBorderStyle);
                }
                long answerNum = 1;
                for (Answer answer : answers) {
                    XSSFRow answerRow = answerNum == 1 ? row : sheet.createRow(rows++);
                    answerRow.setHeight(REGULAR_ROW_HEIGHT);

                    XSSFCell answerTextCell = answerRow.createCell(ANSWER_OPTIONS_COLUMN_INDEX, CellType.STRING);
                    answerTextCell.setCellValue(answer.getAnswerText());
                    answerTextCell.setCellStyle(answers.size() == answerNum ? cellBottomBorderAndWrapTextStyle : cellWrapTextStyle);

                    XSSFCell candidateAnswerCell = answerRow.createCell(CANDIDATE_ANSWER_COLUMN_INDEX, CellType.STRING);
                    candidateAnswerCell.setCellValue(answer.isSelected() ? "X" : "");

                    XSSFCell rightAnswerCell = answerRow.createCell(RIGHT_ANSWER_COLUMN_INDEX, CellType.STRING);
                    rightAnswerCell.setCellValue(question.getRightAnswersIds().contains(answer.getId()) ? "X" : "");

                    XSSFCell answerResultCell = answerRow.createCell(RESULT_COLUMN_INDEX, CellType.STRING);
                    String answerResult = question.getRightAnswersIds().contains(answer.getId()) ? "верно" : "неверно";
                    answerResultCell.setCellValue(answer.isSelected() ? answerResult : "");

                    if (answerNum == 1) {
                        XSSFCell answerPointsCell = answerRow.createCell(POINTS_COLUMN_INDEX, CellType.STRING);
                        answerPointsCell.setCellValue(String.format("%.2f", question.getScore()));
                    }

                    // нижняя граница после каждого вопроса
                    if (answerNum == answers.size()) {
                        if (answerNum != 1) {
                            XSSFCell emptyIdCell = answerRow.createCell(ID_COLUMN_INDEX, CellType.STRING);
                            XSSFCell emptyQuestionCell = answerRow.createCell(QUESTION_TEXT_COLUMN_INDEX, CellType.STRING);
                            XSSFCell emptyPointsCell = answerRow.createCell(POINTS_COLUMN_INDEX, CellType.STRING);
                            emptyIdCell.setCellStyle(cellBottomBorderStyle);
                            emptyQuestionCell.setCellStyle(cellBottomBorderAndWrapTextStyle);
                            emptyPointsCell.setCellStyle(cellBottomBorderStyle);
                        }
                        candidateAnswerCell.setCellStyle(cellBottomBorderStyle);
                        rightAnswerCell.setCellStyle(cellBottomBorderStyle);
                        answerResultCell.setCellStyle(cellBottomBorderStyle);
                    }
                    answerNum++;
                }

            }
        }
        addResultPointsRow(result, sheet);
        writeDataToFile();
    }

    private void createTitleRow(XSSFSheet sheet) {
        XSSFRow mainRow = sheet.createRow(rows++);

        XSSFCellStyle mainCellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        mainCellStyle.setFont(font);
        mainCellStyle.setBorderBottom(BorderStyle.THICK);
        mainCellStyle.setWrapText(true);
        mainRow.setHeight(MAIN_ROW_HEIGHT);

        XSSFCell cell = mainRow.createCell(ID_COLUMN_INDEX, CellType.STRING);
        cell.setCellValue("Номер вопроса");
        cell.setCellStyle(mainCellStyle);
        XSSFCell cell1 = mainRow.createCell(QUESTION_TEXT_COLUMN_INDEX, CellType.STRING);
        cell1.setCellValue("Текст вопроса");
        cell1.setCellStyle(mainCellStyle);
        XSSFCell cell2 = mainRow.createCell(ANSWER_OPTIONS_COLUMN_INDEX, CellType.STRING);
        cell2.setCellValue("Варианты ответов");
        cell2.setCellStyle(mainCellStyle);
        XSSFCell cell3 = mainRow.createCell(CANDIDATE_ANSWER_COLUMN_INDEX, CellType.STRING);
        cell3.setCellValue("Ответ кандидата");
        cell3.setCellStyle(mainCellStyle);
        XSSFCell cell4 = mainRow.createCell(RIGHT_ANSWER_COLUMN_INDEX, CellType.STRING);
        cell4.setCellValue("Правильный ответ");
        cell4.setCellStyle(mainCellStyle);
        XSSFCell cell5 = mainRow.createCell(RESULT_COLUMN_INDEX, CellType.STRING);
        cell5.setCellValue("Итог");
        cell5.setCellStyle(mainCellStyle);
        XSSFCell cell6 = mainRow.createCell(POINTS_COLUMN_INDEX, CellType.STRING);
        cell6.setCellValue("Сумма");
        cell6.setCellStyle(mainCellStyle);
    }

    private XSSFSheet createFormattedSheet() {
        XSSFSheet sheet = workbook.createSheet();
        sheet.setColumnWidth(ID_COLUMN_INDEX, 8*256);
        sheet.setColumnWidth(QUESTION_TEXT_COLUMN_INDEX, 70*256);
        sheet.setColumnWidth(ANSWER_OPTIONS_COLUMN_INDEX, 70*256);
        sheet.setColumnWidth(CANDIDATE_ANSWER_COLUMN_INDEX, 11*256);
        sheet.setColumnWidth(RIGHT_ANSWER_COLUMN_INDEX, 12*256);
        sheet.setColumnWidth(RESULT_COLUMN_INDEX, 8*256);
        sheet.setColumnWidth(POINTS_COLUMN_INDEX, 7*256);
        return sheet;
    }

    private XSSFCellStyle getBottomBorderStyle() {
        XSSFCellStyle cellBottomBorderStyle = workbook.createCellStyle();
        cellBottomBorderStyle.setBorderBottom(BorderStyle.THIN);
        return cellBottomBorderStyle;
    }

    private XSSFCellStyle getBorderAndWrapTextlStyle() {
        XSSFCellStyle cellBottomBorderAndWrapTextStyle = workbook.createCellStyle();
        cellBottomBorderAndWrapTextStyle.setWrapText(true);
        cellBottomBorderAndWrapTextStyle.setBorderBottom(BorderStyle.THIN);
        return cellBottomBorderAndWrapTextStyle;
    }

    private XSSFCellStyle getWrapStyle() {
        XSSFCellStyle cellWrapTextStyle = workbook.createCellStyle();
        cellWrapTextStyle.setWrapText(true);
        return cellWrapTextStyle;
    }

    private void writeDataToFile() throws IOException {
        Path excelFile = Paths.get(String.format(FILE_NAME_PATTERN, lastName, firstName));
        Files.createFile(excelFile);
        try (OutputStream stream = Files.newOutputStream(excelFile)) {
            workbook.write(stream);
        }
    }

    private void addResultPointsRow(double result, XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(rows);
        XSSFCellStyle mainCellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        mainCellStyle.setFont(font);
        row.createCell(6, CellType.STRING).setCellValue(String.format("%.2f", result));
        row.createCell(5, CellType.STRING).setCellValue("Итого");
    }

    private void addNameTimeRows(XSSFSheet sheet) {
        sheet.createRow(rows++)
                .createCell(1, CellType.STRING)
                .setCellValue(String.format("Фамилия: %s", lastName));
        sheet.createRow(rows++)
                .createCell(1, CellType.STRING)
                .setCellValue(String.format("Имя: %s", firstName));
        sheet.createRow(rows++)
                .createCell(1, CellType.STRING)
                .setCellValue(String.format("Время окончания теста: %s", LocalDateTime.now(ZoneId.of("Europe/Moscow"))
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
        sheet.createRow(rows++);
    }
}
