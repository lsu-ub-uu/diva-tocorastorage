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
package se.uu.ub.cora.diva.tocorastorage;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class DivaToCoraRecordInfoConverter {
	private XMLXPathParser parser;
	private DataGroup recordInfo;

	public DivaToCoraRecordInfoConverter(XMLXPathParser parser) {
		this.parser = parser;
	}

	public static DataGroup createRecordInfo(XMLXPathParser parser) {
		DivaToCoraRecordInfoConverter alvinToCoraRecordInfoConverter = new DivaToCoraRecordInfoConverter(
				parser);
		return alvinToCoraRecordInfoConverter.createRecordInfoAsDataGroup();
	}

	private DataGroup createRecordInfoAsDataGroup() {
		recordInfo = DataGroup.withNameInData("recordInfo");
		addType();
		parseAndAddId();
		addDataDivider();
		addCreatedBy();
		parseAndAddTsCreated();
		addUpdatedBy();
		parseAndAddTsUpdated();
		return recordInfo;
	}

	private void addType() {
		DataGroup type = createLinkWithNameInDataAndTypeAndId("type", "recordType", "person");
		recordInfo.addChild(type);
	}

	private static DataGroup createLinkWithNameInDataAndTypeAndId(String nameInData,
			String linkedRecordType, String linkedRecordId) {
		DataGroup type = DataGroup.withNameInData(nameInData);
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordType));
		type.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
		return type;
	}

	private void parseAndAddId() {
		String pid = parser.getStringFromDocumentUsingXPath("/authorityPerson/pid/text()");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", pid));
	}

	private void addDataDivider() {
		DataGroup dataDivider = createLinkWithNameInDataAndTypeAndId("dataDivider", "system",
				"diva");
		recordInfo.addChild(dataDivider);
	}

	private void addCreatedBy() {
		DataGroup createdBy = createLinkWithNameInDataAndTypeAndId("createdBy", "user", "12345");
		recordInfo.addChild(createdBy);
	}

	private void parseAndAddTsCreated() {
		String tsCreatedWithLetters = parser.getStringFromDocumentUsingXPath(
				"/authorityPerson/recordInfo/events/event/timestamp/text()");
		String tsCreated = removeTAndZFromTimestamp(tsCreatedWithLetters);
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("tsCreated", tsCreated));
	}

	private String removeTAndZFromTimestamp(String tsCreatedWithLetters) {
		return tsCreatedWithLetters.replace("T", " ").replace("Z", "");
	}

	private void addUpdatedBy() {
		DataGroup updatedBy = createLinkWithNameInDataAndTypeAndId("updatedBy", "user", "12345");
		recordInfo.addChild(updatedBy);
	}

	private void parseAndAddTsUpdated() {
		String tsUpdatedWithLetters = getLastTsUpdatedFromDocument();
		String tsUpdated = removeTAndZFromTimestamp(tsUpdatedWithLetters);

		recordInfo.addChild(DataAtomic.withNameInDataAndValue("tsUpdated", tsUpdated));
	}

	private String getLastTsUpdatedFromDocument() {
		NodeList list = parser.getNodeListFromDocumentUsingXPath(
				"/authorityPerson/recordInfo/events/event/timestamp/text()");
		Node item = getTheLastTsUpdatedAsItShouldBeTheLatest(list);
		return item.getTextContent();
	}

	private Node getTheLastTsUpdatedAsItShouldBeTheLatest(NodeList list) {
		return list.item(list.getLength() - 1);
	}

}
