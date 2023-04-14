package ru.rsmu.olympreg.pages.control;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.dao.OlympiadDao;
import ru.rsmu.olympreg.entities.OlympiadConfig;
import ru.rsmu.olympreg.entities.SchoolLocation;
import ru.rsmu.olympreg.entities.SubjectRegion;
import ru.rsmu.olympreg.utils.YearHelper;
import ru.rsmu.olympreg.viewentities.download.AttachmentExcel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author leonid.
 */
public class FinalStatistics {
    private static final Logger logger = LoggerFactory.getLogger( FinalStatistics.class );

    @Inject
    private OlympiadDao olympiadDao;

    @Inject
    private CompetitorDao competitorDao;

    public StreamResponse onActivate() {
        int actualYear = YearHelper.getActualYear();
        List<OlympiadConfig> configs = olympiadDao.getAllCurrentConfigs( actualYear );
        if ( configs.isEmpty() ) return null;

        List<SubjectRegion> allRegions = olympiadDao.findAll( SubjectRegion.class );
        allRegions.sort( Comparator.comparingInt( SubjectRegion::getCode ) );
        HSSFWorkbook workbook = new HSSFWorkbook();

        for ( OlympiadConfig config : configs ) {
            HSSFSheet sheet = workbook.createSheet( config.getSubject().name() +" " + config.getClassNumber() + " 1st stage");
            // 1st stage
            //participations
            Map<SubjectRegion, FinalStats> participationFirst = getStats( config, 0, 0 );
            //pass to 2nd
            Map<SubjectRegion, FinalStats> prizesFirst = getStats( config, 0, config.getSecondStagePassScore() );
            //winners
            Map<SubjectRegion, FinalStats> winnersFirst = getStats( config, 0, config.getFirstStageWinnerScore() );

            populate( sheet, allRegions, participationFirst, prizesFirst, winnersFirst );

            sheet = workbook.createSheet( config.getSubject().name() +" " + config.getClassNumber() + " 2nd stage");
            List<Integer> top3 = competitorDao.getTop3Results( config.getSubject(), config.getClassNumber(), actualYear );
            int winner = top3.isEmpty() ? 0 : top3.get( 0 );
            int prize = top3.isEmpty() ? 0 : top3.get( top3.size() - 1 );
            // 2nd stage
            Map<SubjectRegion, FinalStats> participationSecond = getStats( config, 1, 0 );
            // prize
            Map<SubjectRegion, FinalStats> prizesSecond = getStats( config, 1, prize );
            // winners
            Map<SubjectRegion, FinalStats> winnersSecond = getStats( config, 1, winner );

            populate( sheet, allRegions, participationSecond, prizesSecond, winnersSecond );
        }
        //convert table to bytearray and return
        ByteArrayOutputStream document = new ByteArrayOutputStream();
        try {
            workbook.write(document);
        } catch (IOException ie) {
            logger.error( "Can't create document", ie );
        }

        return new AttachmentExcel(document.toByteArray(), "Final_Statistics_" + actualYear);
    }

    private void populate( HSSFSheet sheet, List<SubjectRegion> regions,
                           Map<SubjectRegion, FinalStats> participation,
                           Map<SubjectRegion, FinalStats> prizes,
                           Map<SubjectRegion, FinalStats> winners ) {
        Row rowTitle = sheet.createRow(0);
        Cell cell = rowTitle.createCell( 0 );
        cell.setCellValue( "Регион" );
        cell = rowTitle.createCell( 1 );
        cell.setCellValue( "Участники" );
        cell = rowTitle.createCell( 3 );
        cell.setCellValue( "Призеры" );
        cell = rowTitle.createCell( 5 );
        cell.setCellValue( "Победители" );
        rowTitle = sheet.createRow(1);
        cell = rowTitle.createCell( 1 );
        cell.setCellValue( "все" );
        cell = rowTitle.createCell( 2 );
        cell.setCellValue( "из сельской местности" );
        cell = rowTitle.createCell( 3 );
        cell.setCellValue( "все" );
        cell = rowTitle.createCell( 4 );
        cell.setCellValue( "из сельской местности" );
        cell = rowTitle.createCell( 5 );
        cell.setCellValue( "все" );
        cell = rowTitle.createCell( 6 );
        cell.setCellValue( "из сельской местности" );
        sheet.addMergedRegion( new CellRangeAddress( 0, 1, 0, 0 ) );
        sheet.addMergedRegion( new CellRangeAddress( 0, 0, 1, 2 ) );
        sheet.addMergedRegion( new CellRangeAddress( 0, 0, 3, 4 ) );
        sheet.addMergedRegion( new CellRangeAddress( 0, 0, 5, 6 ) );

        int rowN = 2;
        for ( SubjectRegion region : regions ) {
            Row row = sheet.createRow( rowN++ );
            cell = row.createCell( 0 );
            cell.setCellValue( String.format( "%s (%02d)", region.getName(), region.getCode() ) );
            FinalStats stats = participation.get( region );
            if ( stats != null ) {
                fillStats( row,1, stats, null );
            }
            FinalStats statsWinner = winners.get( region );
            FinalStats statsPrizes = prizes.get( region );
            if ( statsPrizes != null ) {
                fillStats( row, 3, statsPrizes, statsWinner );
            }
            if ( statsWinner != null ) {
                fillStats( row, 5, statsWinner, null );
            }
        }
        // calculate sums
        Row totals = sheet.createRow( rowN );
        cell = totals.createCell( 0 );
        cell.setCellValue( "Итого ="  );
        for ( int i = 1; i <= 6; i++ ) {
            char c = (char) ('A' + i);
            cell = totals.createCell( i );
            cell.setCellFormula( String.format( "SUM(%s3:%s%d)", c, c, rowN ) );
        }

        sheet.autoSizeColumn( 0 );
        sheet.autoSizeColumn( 2 );
        sheet.autoSizeColumn( 4 );
        sheet.autoSizeColumn( 6 );
    }

    private void fillStats( Row row, int offset, FinalStats stats, FinalStats minus ) {
        Cell cell = row.createCell( offset );
        cell.setCellValue( stats.getCityCount() + stats.getCountryCount() -
                (minus != null ? (minus.getCityCount() + minus.getCountryCount()) : 0 ) );
        cell = row.createCell( offset + 1 );
        cell.setCellValue( stats.getCountryCount() - (minus != null ? minus.getCountryCount() : 0) );
    }

    private Map<SubjectRegion,FinalStats> getStats( OlympiadConfig config, int stage, int score ) {
        List<Object[]> results = competitorDao.findFinalStatistics( config, stage, score );
        Map<SubjectRegion,FinalStats> statsMap = new HashMap<>();
        for ( Object[] result : results ) {
            SubjectRegion theRegion = (SubjectRegion) result[0];
            FinalStats stats = statsMap.computeIfAbsent( theRegion, FinalStats::new );
            SchoolLocation location = (SchoolLocation) result[1];
            Long count = (Long) result[2];
            if ( location == SchoolLocation.LOCATION_COUNTRY ) {
                stats.setCountryCount( count.intValue() );
            }
            else {
                stats.setCityCount( count.intValue() );
            }
        }
        return statsMap;
    }

    public static class FinalStats {
        private final SubjectRegion region;
        private int cityCount;
        private int countryCount;

        public FinalStats( SubjectRegion region ) {
            this.region = region;
        }

        public SubjectRegion getRegion() {
            return region;
        }

        public int getCityCount() {
            return cityCount;
        }

        public void setCityCount( int cityCount ) {
            this.cityCount = cityCount;
        }

        public int getCountryCount() {
            return countryCount;
        }

        public void setCountryCount( int countryCount ) {
            this.countryCount = countryCount;
        }
    }
}
