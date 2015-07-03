package org.mtransit.parser.ca_l_assomption_mrclasso_bus;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MSpec;
import org.mtransit.parser.mt.data.MTrip;

// https://www.amt.qc.ca/en/about/open-data
// http://www.amt.qc.ca/xdata/mrclasso/google_transit.zip
public class LAssomptionMRCLASSOBusAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-l-assomption-mrclasso-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new LAssomptionMRCLASSOBusAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("Generating MRCLASSO bus data...\n");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("Generating MRCLASSO bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	private static final Pattern SECTEUR = Pattern.compile("(secteur[s]? )", Pattern.CASE_INSENSITIVE);
	private static final String SECTEUR_REPLACEMENT = "";

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.route_long_name;
		routeLongName = MSpec.SAINT.matcher(routeLongName).replaceAll(MSpec.SAINT_REPLACEMENT);
		routeLongName = SECTEUR.matcher(routeLongName).replaceAll(SECTEUR_REPLACEMENT);
		return MSpec.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR = "00718F";

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_E680AD = "E680AD";
	private static final String COLOR_A1A1A4 = "A1A1A4";
	private static final String COLOR_99CB9A = "99CB9A";
	private static final String COLOR_EF3B3A = "EF3B3A";
	private static final String COLOR_E8B909 = "E8B909";
	private static final String COLOR_067650 = "067650";
	private static final String COLOR_1DA1DC = "1DA1DC";
	private static final String COLOR_AAB41C = "AAB41C";
	private static final String COLOR_D68119 = "D68119";
	private static final String COLOR_A686AA = "A686AA";
	private static final String COLOR_A74232 = "A74232";
	private static final String COLOR_FDE900 = "FDE900";
	private static final String COLOR_623F99 = "623F99";

	private static final String RSN_1 = "1";
	private static final String RSN_2 = "2";
	private static final String RSN_5 = "5";
	private static final String RSN_6 = "6";
	private static final String RSN_8 = "8";
	private static final String RSN_9 = "9";
	private static final String RSN_11 = "11";
	private static final String RSN_14 = "14";
	private static final String RSN_15 = "15";
	private static final String RSN_100 = "100";
	private static final String RSN_101 = "101";
	private static final String RSN_200 = "200";
	private static final String RSN_300 = "300";
	private static final String RSN_400 = "400";

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (RSN_1.equals(gRoute.route_short_name)) return COLOR_AAB41C;
		if (RSN_2.equals(gRoute.route_short_name)) return COLOR_E680AD;
		if (RSN_5.equals(gRoute.route_short_name)) return COLOR_A1A1A4;
		if (RSN_6.equals(gRoute.route_short_name)) return COLOR_99CB9A;
		if (RSN_8.equals(gRoute.route_short_name)) return COLOR_EF3B3A;
		if (RSN_9.equals(gRoute.route_short_name)) return COLOR_E8B909;
		if (RSN_11.equals(gRoute.route_short_name)) return COLOR_067650;
		if (RSN_14.equals(gRoute.route_short_name)) return COLOR_1DA1DC;
		if (RSN_15.equals(gRoute.route_short_name)) return COLOR_AAB41C;
		if (RSN_100.equals(gRoute.route_short_name)) return COLOR_D68119;
		if (RSN_101.equals(gRoute.route_short_name)) return COLOR_A686AA;
		if (RSN_200.equals(gRoute.route_short_name)) return COLOR_A74232;
		if (RSN_300.equals(gRoute.route_short_name)) return COLOR_FDE900;
		if (RSN_400.equals(gRoute.route_short_name)) return COLOR_623F99;
		return super.getRouteColor(gRoute);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.trip_headsign), gTrip.direction_id);
	}

	private static final Pattern DIRECTION = Pattern.compile("(direction )", Pattern.CASE_INSENSITIVE);
	private static final String DIRECTION_REPLACEMENT = "";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = DIRECTION.matcher(tripHeadsign).replaceAll(DIRECTION_REPLACEMENT);
		return MSpec.cleanLabelFR(tripHeadsign);
	}

	private static final Pattern START_WITH_FACE_A = Pattern.compile("^(face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE_AU = Pattern.compile("^(face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE = Pattern.compile("^(face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern SPACE_FACE_A = Pattern.compile("( face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE_AU = Pattern.compile("( face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE = Pattern.compile("( face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern[] START_WITH_FACES = new Pattern[] { START_WITH_FACE_A, START_WITH_FACE_AU, START_WITH_FACE };

	private static final Pattern[] SPACE_FACES = new Pattern[] { SPACE_FACE_A, SPACE_WITH_FACE_AU, SPACE_WITH_FACE };

	private static final Pattern AVENUE = Pattern.compile("( avenue)", Pattern.CASE_INSENSITIVE);
	private static final String AVENUE_REPLACEMENT = " av.";

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = AVENUE.matcher(gStopName).replaceAll(AVENUE_REPLACEMENT);
		gStopName = Utils.replaceAll(gStopName, START_WITH_FACES, MSpec.SPACE);
		gStopName = Utils.replaceAll(gStopName, SPACE_FACES, MSpec.SPACE);
		return super.cleanStopNameFR(gStopName);
	}

	private static final String ZERO = "0";

	@Override
	public String getStopCode(GStop gStop) {
		if (ZERO.equals(gStop.stop_code)) {
			return null;
		}
		return super.getStopCode(gStop);
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	@Override
	public int getStopId(GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (stopCode != null && stopCode.length() > 0) {
			return Integer.valueOf(stopCode); // using stop code as stop ID
		}
		// generating integer stop ID
		Matcher matcher = DIGITS.matcher(gStop.stop_id);
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		int stopId;
		System.out.println("Stop doesn't have an ID (start with)! " + gStop);
		System.exit(-1);
		stopId = -1;
		System.out.println("Stop doesn't have an ID (end with)! " + gStop);
		System.exit(-1);
		return stopId + digits;
	}

}
