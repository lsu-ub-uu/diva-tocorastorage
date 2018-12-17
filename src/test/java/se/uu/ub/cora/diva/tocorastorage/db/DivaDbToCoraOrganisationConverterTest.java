package se.uu.ub.cora.diva.tocorastorage.db;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class DivaDbToCoraOrganisationConverterTest {

	private DivaDbToCoraOrganisationConverter converter;
	private Map<String, String> rowFromDb;

	@BeforeMethod
	public void beforeMethod() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("id", "someOrgId");
		converter = new DivaDbToCoraOrganisationConverter();
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation to Cora organisation: Map does not contain value for id")
	public void testEmptyMap() {
		rowFromDb = new HashMap<>();
		DataGroup organisation = converter.fromMap(rowFromDb);
		assertNull(organisation);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation to Cora organisation: Map does not contain value for id")
	public void testMapWithEmptyValueThrowsError() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("id", "");
		converter.fromMap(rowFromDb);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation to Cora organisation: Map does not contain value for id")
	public void testMapWithNonEmptyValueANDEmptyValueThrowsError() {
		Map<String, String> rowFromDb = new HashMap<>();
		rowFromDb.put("defaultname", "someName");
		rowFromDb.put("id", "");
		converter.fromMap(rowFromDb);
	}

	@Test(expectedExceptions = ConversionException.class, expectedExceptionsMessageRegExp = ""
			+ "Error converting organisation to Cora organisation: Map does not contain value for id")
	public void mapDoesNotContainOrganisationIdValue() {
		rowFromDb = new HashMap<>();
		rowFromDb.put("defaultname", "someName");
		converter.fromMap(rowFromDb);
	}

	@Test
	public void testMinimalValuesReturnsDataGroupWithCorrectRecordInfo() {
		DataGroup organisation = converter.fromMap(rowFromDb);
		assertEquals(organisation.getNameInData(), "organisation");

		assertCorrectRecordInfoWithId(organisation, "someOrgId");
	}

	@Test
	public void testOrganisationName() {
		rowFromDb.put("defaultname", "Java-fakulteten");
		DataGroup organisation = converter.fromMap(rowFromDb);
		assertEquals(organisation.getNameInData(), "organisation");
		assertEquals(organisation.getFirstAtomicValueWithNameInData("organisationName"),
				"Java-fakulteten");
	}

	@Test
	public void testTypeCode() {
		rowFromDb.put("type_code", "unit");
		DataGroup organisation = converter.fromMap(rowFromDb);
		assertEquals(organisation.getFirstAtomicValueWithNameInData("organisationType"), "unit");
	}

	@Test
	public void testAlternativeName() {
		rowFromDb.put("alternative_name", "Java Faculty");
		DataGroup organisation = converter.fromMap(rowFromDb);
		assertTrue(organisation.containsChildWithNameInData("alternativeName"));
		DataGroup alternativeName = organisation.getFirstGroupWithNameInData("alternativeName");
		assertEquals(alternativeName.getFirstAtomicValueWithNameInData("language"), "en");
		assertEquals(alternativeName.getFirstAtomicValueWithNameInData("organisationName"),
				"Java Faculty");
	}

	@Test
	public void testOrganisationNotEligible() {
		rowFromDb.put("not_eligible", "t");
		DataGroup organisation = converter.fromMap(rowFromDb);
		assertEquals(organisation.getFirstAtomicValueWithNameInData("eligible"), "no");
	}

	@Test
	public void testOrganisationEligible() {
		rowFromDb.put("not_eligible", "f");
		DataGroup organisation = converter.fromMap(rowFromDb);
		assertEquals(organisation.getFirstAtomicValueWithNameInData("eligible"), "yes");
	}

	private void assertCorrectRecordInfoWithId(DataGroup organisation, String id) {
		DataGroup recordInfo = organisation.getFirstGroupWithNameInData("recordInfo");
		assertEquals(recordInfo.getFirstAtomicValueWithNameInData("id"), id);

		DataGroup type = recordInfo.getFirstGroupWithNameInData("type");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordType"), "recordType");
		assertEquals(type.getFirstAtomicValueWithNameInData("linkedRecordId"), "divaOrganisation");

		DataGroup dataDivider = recordInfo.getFirstGroupWithNameInData("dataDivider");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordType"), "system");
		assertEquals(dataDivider.getFirstAtomicValueWithNameInData("linkedRecordId"), "diva");

	}

}
