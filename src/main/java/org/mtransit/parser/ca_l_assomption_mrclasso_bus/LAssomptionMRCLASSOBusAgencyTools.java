package org.mtransit.parser.ca_l_assomption_mrclasso_bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.Utils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTripStop;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.mt.data.MTrip;

// https://rtm.quebec/en/about/open-data
// https://rtm.quebec/xdata/mrclasso/google_transit.zip
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
		System.out.printf("\nGenerating MRCLASSO bus data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this, true);
		super.start(args);
		System.out.printf("\nGenerating MRCLASSO bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIds != null && this.serviceIds.isEmpty();
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

	private static final String T = "T";

	private static final long RID_STARTS_WITH_T = 20_000L;

	@Override
	public long getRouteId(GRoute gRoute) {
		if (!Utils.isDigitsOnly(gRoute.getRouteId())) {
			Matcher matcher = DIGITS.matcher(gRoute.getRouteShortName());
			if (matcher.find()) {
				int digits = Integer.parseInt(matcher.group());
				if (gRoute.getRouteShortName().startsWith(T)) {
					return RID_STARTS_WITH_T + digits;
				}
			}
			System.out.printf("\nUnexpected route ID for %s!\n", gRoute);
			System.exit(-1);
			return -1L;
		}
		return super.getRouteId(gRoute);
	}

	private static final Pattern SECTEUR = Pattern.compile("(secteur[s]? )", Pattern.CASE_INSENSITIVE);
	private static final String SECTEUR_REPLACEMENT = "";

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.getRouteLongName();
		routeLongName = CleanUtils.SAINT.matcher(routeLongName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		routeLongName = SECTEUR.matcher(routeLongName).replaceAll(SECTEUR_REPLACEMENT);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR = "1F1F1F"; // DARK GRAY (from GTFS)

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
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
			if (RSN_1.equals(gRoute.getRouteShortName())) return COLOR_AAB41C;
			if (RSN_2.equals(gRoute.getRouteShortName())) return COLOR_E680AD;
			if (RSN_5.equals(gRoute.getRouteShortName())) return COLOR_A1A1A4;
			if (RSN_6.equals(gRoute.getRouteShortName())) return COLOR_99CB9A;
			if (RSN_8.equals(gRoute.getRouteShortName())) return COLOR_EF3B3A;
			if (RSN_9.equals(gRoute.getRouteShortName())) return COLOR_E8B909;
			if (RSN_11.equals(gRoute.getRouteShortName())) return COLOR_067650;
			if (RSN_14.equals(gRoute.getRouteShortName())) return COLOR_1DA1DC;
			if (RSN_15.equals(gRoute.getRouteShortName())) return COLOR_AAB41C;
			if (RSN_100.equals(gRoute.getRouteShortName())) return COLOR_D68119;
			if (RSN_101.equals(gRoute.getRouteShortName())) return COLOR_A686AA;
			if (RSN_200.equals(gRoute.getRouteShortName())) return COLOR_A74232;
			if (RSN_300.equals(gRoute.getRouteShortName())) return COLOR_FDE900;
			if (RSN_400.equals(gRoute.getRouteShortName())) return COLOR_623F99;
			System.out.println("Unexpected route color " + gRoute);
			System.exit(-1);
			return null;
		}
		return super.getRouteColor(gRoute);
	}

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;
	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<Long, RouteTripSpec>();
		map2.put(RID_STARTS_WITH_T + 1L, new RouteTripSpec(RID_STARTS_WITH_T + 1L, // T1
				0, MTrip.HEADSIGN_TYPE_STRING, "Repentigny", //
				1, MTrip.HEADSIGN_TYPE_STRING, "Charlemagne") //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"87761", // rue Carufel / rue du Sacré-Coeur
								"87555", // boul. Claude-David / rue Notre-Dame
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						/* no stops */
						})) //
				.compileBothTripSort());
		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public int compareEarly(long routeId, List<MTripStop> list1, List<MTripStop> list2, MTripStop ts1, MTripStop ts2, GStop ts1GStop, GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop, this);
		}
		return super.compareEarly(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
	}

	@Override
	public ArrayList<MTrip> splitTrip(MRoute mRoute, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return ALL_ROUTE_TRIPS2.get(mRoute.getId()).getAllTrips();
		}
		return super.splitTrip(mRoute, gTrip, gtfs);
	}

	@Override
	public Pair<Long[], Integer[]> splitTripStop(MRoute mRoute, GTrip gTrip, GTripStop gTripStop, ArrayList<MTrip> splitTrips, GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()), this);
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), gTrip.getDirectionId());
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		List<String> headsignsValues = Arrays.asList(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignValue());
		if (mTrip.getRouteId() == 2L) {
			if (Arrays.asList( //
					"St-Sulpice", //
					"Lavaltrie" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Lavaltrie", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 6L) {
			if (Arrays.asList( //
					"Repentigny", //
					"Épiphanie" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Épiphanie", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 400L) {
			if (Arrays.asList( //
					"Repentigny", //
					"Montréal" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Montréal", mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					"Repentigny", //
					"Assomption" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Assomption", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == RID_STARTS_WITH_T + 3L) { // T3
			if (Arrays.asList( //
					"Ligne 9", //
					"T3" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("T3", mTrip.getHeadsignId());
				return true;
			}
		}
		System.out.printf("\nUnexpected trips to merge %s & %s!\n", mTrip, mTripToMerge);
		System.exit(-1);
		return false;
	}

	private static final Pattern DIRECTION = Pattern.compile("(direction )", Pattern.CASE_INSENSITIVE);
	private static final String DIRECTION_REPLACEMENT = "";

	private static final Pattern TAXIBUS_T_ = Pattern.compile("((^|\\W)(taxibus t([\\d]+))(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String TAXIBUS_T_REPLACEMENT = "$2" + "T$4" + "$5";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = CleanUtils.keepToFR(tripHeadsign);
		tripHeadsign = DIRECTION.matcher(tripHeadsign).replaceAll(DIRECTION_REPLACEMENT);
		tripHeadsign = TAXIBUS_T_.matcher(tripHeadsign).replaceAll(TAXIBUS_T_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
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
		gStopName = Utils.replaceAll(gStopName, START_WITH_FACES, CleanUtils.SPACE);
		gStopName = Utils.replaceAll(gStopName, SPACE_FACES, CleanUtils.SPACE);
		return CleanUtils.cleanLabelFR(gStopName);
	}

	private static final String ZERO = "0";

	@Override
	public String getStopCode(GStop gStop) {
		if (ZERO.equals(gStop.getStopCode())) {
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
		Matcher matcher = DIGITS.matcher(gStop.getStopId());
		if (matcher.find()) {
			int digits = Integer.parseInt(matcher.group());
			int stopId;
			System.out.printf("\nStop doesn't have an ID (start with) %s!\n", gStop);
			System.exit(-1);
			stopId = -1;
			System.out.printf("\nStop doesn't have an ID (end with) %s!\n", gStop);
			System.exit(-1);
			return stopId + digits;
		}
		System.out.printf("\nUnexpected stop ID for %s!\n", gStop);
		System.exit(-1);
		return -1;
	}
}