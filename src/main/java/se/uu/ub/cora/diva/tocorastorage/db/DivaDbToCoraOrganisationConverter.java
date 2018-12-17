package se.uu.ub.cora.diva.tocorastorage.db;

import java.util.Map;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class DivaDbToCoraOrganisationConverter implements DivaDbToCoraConverter {

	private static final String ORGANISATION_ID = "id";
	private static final String ALTERNATIVE_NAME = "alternative_name";

	@Override
	public DataGroup fromMap(Map<String, String> map) {
		if (organisationIsEmpty(map)) {
			throw ConversionException.withMessageAndException(
					"Error converting organisation to Cora organisation: Map does not contain value for "
							+ ORGANISATION_ID,
					null);
		}
		return createDataGroup(map);

	}

	private boolean organisationIsEmpty(Map<String, String> map) {
		return !map.containsKey(ORGANISATION_ID) || "".equals(map.get(ORGANISATION_ID));
	}

	private DataGroup createDataGroup(Map<String, String> map) {
		DataGroup organisation = createAndAddOrganisationWithRecordInfo(map);
		createAndAddName(map, organisation);
		createAndAddAlternativeName(map, organisation);
		createAndAddOrganisationType(organisation);
		createAndAddEligibility(map, organisation);
		return organisation;
	}

	private DataGroup createAndAddOrganisationWithRecordInfo(Map<String, String> map) {
		DataGroup organisation = DataGroup.withNameInData("organisation");
		String id = map.get(ORGANISATION_ID);
		DataGroup recordInfo = createRecordInfo(id);
		organisation.addChild(recordInfo);
		return organisation;
	}

	private void createAndAddName(Map<String, String> map, DataGroup organisation) {
		String divaOrganisationName = map.get("defaultname");
		organisation.addChild(
				DataAtomic.withNameInDataAndValue("organisationName", divaOrganisationName));
	}

	private void createAndAddAlternativeName(Map<String, String> map, DataGroup organisation) {
		DataGroup alternativeNameDataGroup = DataGroup.withNameInData("alternativeName");
		alternativeNameDataGroup.addChild(DataAtomic.withNameInDataAndValue("language", "en"));
		String alternativeName = map.get(ALTERNATIVE_NAME);
		alternativeNameDataGroup
				.addChild(DataAtomic.withNameInDataAndValue("organisationName", alternativeName));
		organisation.addChild(alternativeNameDataGroup);
	}

	private void createAndAddOrganisationType(DataGroup organisation) {
		organisation.addChild(DataAtomic.withNameInDataAndValue("organisationType", "unit"));
	}

	private void createAndAddEligibility(Map<String, String> map, DataGroup organisation) {
		String eligible = map.get("not_eligible");
		String coraEligible = isEligible(eligible) ? "yes" : "no";
		organisation.addChild(DataAtomic.withNameInDataAndValue("eligible", coraEligible));
	}

	private boolean isEligible(String eligible) {
		return "f".equals(eligible);
	}

	private DataGroup createRecordInfo(String id) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", id));
		createAndAddType(recordInfo);
		createAndAddDataDivider(recordInfo);
		return recordInfo;
	}

	private void createAndAddType(DataGroup recordInfo) {
		DataGroup type = DataGroup.withNameInData("type");
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "divaOrganisation"));
		recordInfo.addChild(type);
	}

	private void createAndAddDataDivider(DataGroup recordInfo) {
		DataGroup dataDivider = DataGroup.withNameInData("dataDivider");
		dataDivider.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "system"));
		dataDivider.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", "diva"));
		recordInfo.addChild(dataDivider);
	}
}
