package se.uu.ub.cora.diva.tocorastorage.db;

import java.util.Map;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class DivaDbToCoraOrganisationPredecessorConverter implements DivaDbToCoraConverter {

	private static final String DESCRIPTION = "description";
	private static final String PREDECESSOR_ID = "predecessorid";
	private static final String ORGANISATION_ID = "id";
	private Map<String, String> dbRow;

	@Override
	public DataGroup fromMap(Map<String, String> dbRow) {
		this.dbRow = dbRow;
		if (predecessorIsMissingMandatoryValues()) {
			throw ConversionException.withMessageAndException(
					"Error converting organisation predecessor to Cora organisation predecessor: Map does not "
							+ "contain mandatory values for organisation id and prdecessor id",
					null);
		}
		return createDataGroup();
	}

	private boolean predecessorIsMissingMandatoryValues() {
		return !dbRow.containsKey(ORGANISATION_ID) || "".equals(dbRow.get(ORGANISATION_ID))
				|| !dbRow.containsKey(PREDECESSOR_ID) || "".equals(dbRow.get(PREDECESSOR_ID));
	}

	private DataGroup createDataGroup() {
		DataGroup formerName = DataGroup.withNameInData("formerName");
		addPredecessorLink(formerName);
		possiblyAddDescription(formerName);
		return formerName;
	}

	private void addPredecessorLink(DataGroup formerName) {
		DataGroup predecessor = DataGroup.withNameInData("organisationLink");
		predecessor.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordType", "divaOrganisation"));
		predecessor.addChild(
				DataAtomic.withNameInDataAndValue("linkedRecordId", dbRow.get(PREDECESSOR_ID)));
		formerName.addChild(predecessor);
	}

	private void possiblyAddDescription(DataGroup formerName) {
		if (predecessorHasDescription()) {
			formerName.addChild(DataAtomic.withNameInDataAndValue("organisationComment",
					dbRow.get(DESCRIPTION)));
		}
	}

	private boolean predecessorHasDescription() {
		return dbRow.containsKey(DESCRIPTION) && !"".equals(dbRow.get(DESCRIPTION));
	}
}
