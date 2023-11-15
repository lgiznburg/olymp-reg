package ru.rsmu.olympreg.pages.control;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.OlympiadSubject;
import ru.rsmu.olympreg.entities.ParticipationInfo;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.utils.YearHelper;
import ru.rsmu.olympreg.viewentities.CompetitorFilter;
import ru.rsmu.olympreg.viewentities.SortCriterion;
import ru.rsmu.olympreg.viewentities.download.AttachmentExcel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiresRoles( value = {"admin", "manager", "moderator"}, logical = Logical.OR)
public class ExportProfiles {
    private static final Logger logger = LoggerFactory.getLogger( ExportProfiles.class );

    @Inject
    private CompetitorDao competitorDao;

    public StreamResponse onActivate() {
        CompetitorFilter filter = new CompetitorFilter();
        List<CompetitorProfile> profiles = competitorDao.findProfiles( filter, new ArrayList<SortCriterion>(),
                0, competitorDao.countProfiles( filter ) );
        if ( profiles.isEmpty() ) return null;
        int actualYear = YearHelper.getActualYear();

        HSSFWorkbook workbook = new HSSFWorkbook();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat((short)14 );  // base date format (depends on local)

        HSSFSheet sheet = workbook.createSheet( "profiles");

        Row rowTitle = sheet.createRow(0);
        int cellNumber = 0;
        Cell cellTitle = rowTitle.createCell(cellNumber++);
        cellTitle.setCellValue( "№ личного дела" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "ФИО" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "email" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "Телефон" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "Пол" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "День рождения" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "№ паспорта" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "Дата выдачи паспорта" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "СНИЛС" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "Класс" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "№ школы" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "Расположение школы" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "Регион" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "Страна" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "Загружены сканы" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "Участие в химии" );
        cellTitle = rowTitle.createCell( cellNumber++);
        cellTitle.setCellValue( "Участие в биологии" );

        int rowNumber = 1;
        for ( CompetitorProfile profile : profiles ) {
            Row row = sheet.createRow( rowNumber++ );
            cellNumber = 0;
            User user = profile.getUser();

            Cell cell = row.createCell( cellNumber++ );
            cell.setCellValue( profile.getCaseNumber() );

            cell = row.createCell( cellNumber++ );
            cell.setCellValue( user.getFullName() );

            cell = row.createCell( cellNumber++ );
            cell.setCellValue( user.getUsername() );

            cell = row.createCell( cellNumber++ );
            cell.setCellValue( profile.getPhoneNumber() == null ? "" : profile.getPhoneNumber() );

            cell = row.createCell( cellNumber++ );
            if ( profile.getSex() != null ) {
                cell.setCellValue( Objects.equals( profile.getSex(), "F" ) ? "Жен." : "Муж." );
            }

            cell = row.createCell( cellNumber++ );
            if ( profile.getBirthDate() != null ) {
                cell.setCellValue( profile.getBirthDate() );
                cell.setCellStyle( dateStyle );
            }

            cell = row.createCell( cellNumber++ );
            cell.setCellValue( profile.getPassportNumber() == null ? "" : profile.getPassportNumber() );

            cell = row.createCell( cellNumber++ );
            if ( profile.getPassportDate() != null ) {
                cell.setCellValue( profile.getPassportDate() );
                cell.setCellStyle( dateStyle );
            }

            cell = row.createCell( cellNumber++ );
            cell.setCellValue( profile.getSnils() == null ? "" : profile.getSnils() );

            cell = row.createCell( cellNumber++ );
            if ( profile.getClassNumber() != null ) {
                cell.setCellValue(  profile.getClassNumber() );
            }

            cell = row.createCell( cellNumber++ );
            cell.setCellValue( profile.getSchoolNumber() == null ? "" : profile.getSchoolNumber() );

            cell = row.createCell( cellNumber++ );
            if ( profile.getSchoolLocation() != null ) {
                cell.setCellValue( profile.getSchoolLocation().getTranslated() );
            }

            cell = row.createCell( cellNumber++ );
            if ( profile.getRegion() != null ) {
                cell.setCellValue( profile.getRegion().getName() );
            }

            cell = row.createCell( cellNumber++ );
            if ( profile.isForeignCountry() ) {
                cell.setCellValue( profile.getCountry() == null ? "" : profile.getCountry().getShortName() );
            }

            cell = row.createCell( cellNumber++ );
            cell.setCellValue( profile.isAttachmentsCompleted() ? "Да" : "Нет" );

            boolean chemistry = false;
            boolean biology = false;
            if ( !profile.getParticipation().isEmpty() ){
                for ( ParticipationInfo participationInfo : profile.getParticipation() ) {
                    if ( participationInfo.getOlympiadSubject().equals(OlympiadSubject.CHEMISTRY) ){
                        chemistry = true;
                    }
                    else if ( participationInfo.getOlympiadSubject().equals(OlympiadSubject.BIOLOGY) ) {
                        biology = true;
                    }
                }
            }
            cell = row.createCell( cellNumber++ );
            cell.setCellValue( chemistry ? "Да" : "Нет" );

            cell = row.createCell( cellNumber++ );
            cell.setCellValue( biology ? "Да" : "Нет" );

        }
        for ( int column = 0; column < cellNumber; column++ ) {
            sheet.autoSizeColumn( column );
        }

        //convert table to bytearray and return
        ByteArrayOutputStream document = new ByteArrayOutputStream();
        try {
            workbook.write(document);
        } catch (IOException ie) {
            logger.error( "Can't create document", ie );
        }

        return new AttachmentExcel(document.toByteArray(), "Competitors_profiles_" + actualYear);
    }
}
