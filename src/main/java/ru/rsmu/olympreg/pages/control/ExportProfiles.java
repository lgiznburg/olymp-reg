package ru.rsmu.olympreg.pages.control;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.dao.internal.CompetitorDaoImpl;
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

@RequiresRoles( value = {"admin", "manager", "moderator"}, logical = Logical.OR)
public class ExportProfiles {
    private static final Logger logger = LoggerFactory.getLogger( ExportProfiles.class );

    @Inject
    private CompetitorDao competitorDao;

    public StreamResponse onActivate() {
        CompetitorFilter filter = new CompetitorFilter();
        List<CompetitorProfile> profiles = competitorDao.findProfiles( filter, new ArrayList<SortCriterion>(),
                1, competitorDao.countProfiles( filter ) );
        if ( profiles.isEmpty() ) return null;
        int actualYear = YearHelper.getActualYear();

        HSSFWorkbook workbook = new HSSFWorkbook();
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
            Row row = sheet.createRow( rowNumber );
            cellNumber = 0;
            User user = profile.getUser();

            Cell cell = rowTitle.createCell( cellNumber++ );
            cell.setCellValue( profile.getCaseNumber() );

            cell = rowTitle.createCell( cellNumber++ );
            cell.setCellValue( user.getFullName() );

            cell = rowTitle.createCell( cellNumber++ );
            cell.setCellValue( user.getUsername() );

            cell = rowTitle.createCell( cellNumber++ );
            cell.setCellValue( profile.getPhoneNumber() );

            cell = rowTitle.createCell( cellNumber++ );
            cell.setCellValue( profile.getSex() );

            cell = rowTitle.createCell( cellNumber++ );
            if ( profile.getBirthDate() != null ) {
                cell.setCellValue( profile.getBirthDate() );
            }

            cell = rowTitle.createCell( cellNumber++ );
            cell.setCellValue( profile.getPassportNumber() );

            cell = rowTitle.createCell( cellNumber++ );
            if ( profile.getPassportDate() != null ) {
                cell.setCellValue( profile.getPassportDate() );
            }

            cell = rowTitle.createCell( cellNumber++ );
            cell.setCellValue( profile.getSnils() );

            cell = rowTitle.createCell( cellNumber++ );
            cell.setCellValue( profile.getClassNumber() );

            cell = rowTitle.createCell( cellNumber++ );
            cell.setCellValue( profile.getSchoolNumber() );

            cell = rowTitle.createCell( cellNumber++ );
            if ( profile.getSchoolLocation() != null ) {
                cell.setCellValue( profile.getSchoolLocation().getTranslated() );
            }

            cell = rowTitle.createCell( cellNumber++ );
            if ( profile.getRegion() != null ) {
                cell.setCellValue( profile.getRegion().getName() );
            }

            cellNumber++;
            if ( profile.isForeignCountry() ) {
                cell = rowTitle.createCell( cellNumber );
                cell.setCellValue( profile.getCountry().getShortName() );
            }

            cell = rowTitle.createCell( cellNumber++ );
            if ( profile.isAttachmentsCompleted() ) {
                cell.setCellValue( "Да" );
            }
            else {
                cell.setCellValue( "Нет" );
            }

            if ( !profile.getParticipation().isEmpty() ){
                for ( ParticipationInfo participationInfo : profile.getParticipation() ) {
                    if ( participationInfo.getOlympiadSubject().equals(OlympiadSubject.CHEMISTRY) ){
                        cell = rowTitle.createCell( cellNumber );
                        cell.setCellValue( "Да" );
                    }
                    else if ( participationInfo.getOlympiadSubject().equals(OlympiadSubject.BIOLOGY) ) {
                        cell = rowTitle.createCell( cellNumber + 1 );
                        cell.setCellValue( "Да" );
                    }
                }
            }
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
