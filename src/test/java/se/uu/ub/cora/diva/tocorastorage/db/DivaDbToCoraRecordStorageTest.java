/*
 * Copyright 2018 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.diva.tocorastorage.db;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.diva.tocorastorage.NotImplementedException;
import se.uu.ub.cora.spider.data.SpiderReadResult;
import se.uu.ub.cora.spider.record.storage.RecordStorage;

public class DivaDbToCoraRecordStorageTest {
	private static final String TABLE_NAME = "organisation";
	private DivaDbToCoraRecordStorage divaToCoraRecordStorage;
	private DivaDbToCoraConverterFactorySpy converterFactory;
	private RecordReaderFactorySpy recordReaderFactory;

	@BeforeMethod
	public void BeforeMethod() {
		converterFactory = new DivaDbToCoraConverterFactorySpy();
		recordReaderFactory = new RecordReaderFactorySpy();
		divaToCoraRecordStorage = DivaDbToCoraRecordStorage
				.usingRecordReaderFactoryAndConverterFactory(recordReaderFactory, converterFactory);
	}

	@Test
	public void testInit() throws Exception {
		assertNotNull(divaToCoraRecordStorage);
	}

	@Test
	public void divaToCoraRecordStorageImplementsRecordStorage() throws Exception {
		assertTrue(divaToCoraRecordStorage instanceof RecordStorage);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "read is not implemented for type: null")
	public void readThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.read(null, null);
	}

	@Test
	public void testReadOrgansiationFactorDbReader() throws Exception {
		divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		assertTrue(recordReaderFactory.factorWasCalled);
	}

	@Test
	public void testReadOrgansiationCountryTableRequestedFromReader() throws Exception {
		divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		assertEquals(recordReader.usedTableName, TABLE_NAME);
	}

	@Test
	public void testReadOrganisationConditionsForOrganisationTable() throws Exception {
		divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		Map<String, String> conditions = recordReader.usedConditions;
		assertEquals(conditions.get("organisation_id"), "someId");
	}

	@Test
	public void testReadOrganisationConverterIsFactored() throws Exception {
		divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		DivaDbToCoraConverter divaDbToCoraConverter = converterFactory.factoredConverters.get(0);
		assertNotNull(divaDbToCoraConverter);
	}

	@Test
	public void testReadOrganisationConverterIsCalledWithDataFromDbStorage() throws Exception {
		divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		DivaDbToCoraConverterSpy divaDbToCoraConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertNotNull(divaDbToCoraConverter.mapToConvert);
		assertEquals(recordReader.returnedList.get(0), divaDbToCoraConverter.mapToConvert);
	}

	@Test
	public void testReadOrganisationCallsDatabaseAndReturnsConvertedResult() throws Exception {
		DataGroup readCountry = divaToCoraRecordStorage.read(TABLE_NAME, "someId");
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		DivaDbToCoraConverterSpy divaDbToCoraConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertEquals(recordReader.returnedList.size(), 1);
		assertEquals(recordReader.returnedList.get(0), divaDbToCoraConverter.mapToConvert);
		assertEquals(readCountry, divaDbToCoraConverter.convertedDbDataGroup);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "create is not implemented")
	public void createThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.create(null, null, null, null, null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "deleteByTypeAndId is not implemented")
	public void deleteByTypeAndIdThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.deleteByTypeAndId(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "linksExistForRecord is not implemented")
	public void linksExistForRecordThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.linksExistForRecord(null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "update is not implemented")
	public void updateThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.update(null, null, null, null, null, null);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "readList is not implemented for type: null")
	public void readListThrowsNotImplementedException() throws Exception {
		divaToCoraRecordStorage.readList(null, null);
	}

	@Test
	public void testReadOrganisationListFactorDbReader() throws Exception {
		divaToCoraRecordStorage.readList(TABLE_NAME, DataGroup.withNameInData("filter"));
		assertTrue(recordReaderFactory.factorWasCalled);
	}

	@Test
	public void testReadOrganisationListCountryTableRequestedFromReader() throws Exception {
		divaToCoraRecordStorage.readList(TABLE_NAME, DataGroup.withNameInData("filter"));
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		assertEquals(recordReader.usedTableName, TABLE_NAME);
	}

	@Test
	public void testReadOrganisationListConverterIsFactored() throws Exception {
		divaToCoraRecordStorage.readList(TABLE_NAME, DataGroup.withNameInData("filter"));
		DivaDbToCoraConverter divaDbToCoraConverter = converterFactory.factoredConverters.get(0);
		assertNotNull(divaDbToCoraConverter);
	}

	@Test
	public void testReadOrganisationListConverterIsCalledWithDataFromDbStorage() throws Exception {
		divaToCoraRecordStorage.readList(TABLE_NAME, DataGroup.withNameInData("filter"));
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		DivaDbToCoraConverterSpy divaDbToCoraConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertNotNull(divaDbToCoraConverter.mapToConvert);
		assertEquals(recordReader.returnedList.get(0), divaDbToCoraConverter.mapToConvert);
	}

	@Test
	public void testReadOrganisationListConverteredIsAddedToList() throws Exception {
		SpiderReadResult spiderReadresult = divaToCoraRecordStorage.readList(TABLE_NAME,
				DataGroup.withNameInData("filter"));
		List<DataGroup> readCountryList = spiderReadresult.listOfDataGroups;
		RecordReaderSpy recordReader = recordReaderFactory.factored;
		DivaDbToCoraConverterSpy divaDbToCoraConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertEquals(recordReader.returnedList.size(), 1);
		assertEquals(recordReader.returnedList.get(0), divaDbToCoraConverter.mapToConvert);
		assertEquals(readCountryList.get(0), divaDbToCoraConverter.convertedDbDataGroup);
	}

	@Test
	public void testReadOrganisationListConverteredMoreThanOneIsAddedToList() throws Exception {
		recordReaderFactory.noOfRecordsToReturn = 3;
		SpiderReadResult spiderReadResult = divaToCoraRecordStorage.readList(TABLE_NAME,
				DataGroup.withNameInData("filter"));
		List<DataGroup> readCountryList = spiderReadResult.listOfDataGroups;
		RecordReaderSpy recordReader = recordReaderFactory.factored;

		assertEquals(recordReader.returnedList.size(), 3);

		DivaDbToCoraConverterSpy divaDbToCoraConverter = (DivaDbToCoraConverterSpy) converterFactory.factoredConverters
				.get(0);
		assertEquals(recordReader.returnedList.get(0), divaDbToCoraConverter.mapToConvert);
		assertEquals(readCountryList.get(0), divaDbToCoraConverter.convertedDbDataGroup);

		// AlvinDbToCoraConverterSpy alvinDbToCoraConverter2 =
		// (AlvinDbToCoraConverterSpy) converterFactory.factoredConverters
		// .get(1);
		// assertEquals(recordReader.returnedList.get(1),
		// alvinDbToCoraConverter2.mapToConvert);
		// assertEquals(readCountryList.get(1),
		// alvinDbToCoraConverter2.convertedDbDataGroup);
		//
		// AlvinDbToCoraConverterSpy alvinDbToCoraConverter3 =
		// (AlvinDbToCoraConverterSpy) converterFactory.factoredConverters
		// .get(2);
		// assertEquals(recordReader.returnedList.get(2),
		// alvinDbToCoraConverter3.mapToConvert);
		// assertEquals(readCountryList.get(2),
		// alvinDbToCoraConverter3.convertedDbDataGroup);

	}

	// @Test(expectedExceptions = NotImplementedException.class,
	// expectedExceptionsMessageRegExp = ""
	// + "readAbstractList is not implemented")
	// public void readAbstractListThrowsNotImplementedException() throws Exception
	// {
	// alvinToCoraRecordStorage.readAbstractList(null, null);
	// }
	//
	// @Test(expectedExceptions = NotImplementedException.class,
	// expectedExceptionsMessageRegExp = ""
	// + "readLinkList is not implemented")
	// public void readLinkListThrowsNotImplementedException() throws Exception {
	// alvinToCoraRecordStorage.readLinkList(null, null);
	// }
	//
	// @Test(expectedExceptions = NotImplementedException.class,
	// expectedExceptionsMessageRegExp = ""
	// + "generateLinkCollectionPointingToRecord is not implemented")
	// public void
	// generateLinkCollectionPointingToRecordThrowsNotImplementedException()
	// throws Exception {
	// alvinToCoraRecordStorage.generateLinkCollectionPointingToRecord(null, null);
	// }
	//
	// @Test(expectedExceptions = NotImplementedException.class,
	// expectedExceptionsMessageRegExp = ""
	// + "recordsExistForRecordType is not implemented")
	// public void recordsExistForRecordTypeThrowsNotImplementedException() throws
	// Exception {
	// alvinToCoraRecordStorage.recordsExistForRecordType(null);
	// }
	//
	// @Test(expectedExceptions = NotImplementedException.class,
	// expectedExceptionsMessageRegExp = ""
	// + "recordExistsForAbstractOrImplementingRecordTypeAndRecordId is not
	// implemented")
	// public void
	// recordExistsForAbstractOrImplementingRecordTypeAndRecordIdThrowsNotImplementedException()
	// throws Exception {
	// alvinToCoraRecordStorage.recordExistsForAbstractOrImplementingRecordTypeAndRecordId(null,
	// null);
	// }
}
