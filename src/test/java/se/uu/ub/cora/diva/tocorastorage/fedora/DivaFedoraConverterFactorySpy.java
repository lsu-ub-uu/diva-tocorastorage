/*
 * Copyright 2018, 2019 Uppsala University Library
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
package se.uu.ub.cora.diva.tocorastorage.fedora;

import java.util.ArrayList;
import java.util.List;

public class DivaFedoraConverterFactorySpy implements DivaFedoraConverterFactory {

	List<DivaFedoraToCoraConverter> factoredConverters = new ArrayList<>();
	List<String> factoredTypes = new ArrayList<>();
	List<DivaCoraToFedoraConverter> factoredToFedoraConverters = new ArrayList<>();
	public List<String> factoredToFedoraTypes = new ArrayList<>();

	@Override
	public DivaFedoraToCoraConverter factorToCoraConverter(String type) {
		factoredTypes.add(type);
		DivaFedoraToCoraConverter converter = new DivaFedoraToCoraConverterSpy();
		factoredConverters.add(converter);
		return converter;
	}

	@Override
	public DivaCoraToFedoraConverter factorToFedoraConverter(String type) {
		factoredToFedoraTypes.add(type);
		DivaCoraToFedoraConverter converter = new DivaCoraToFedoraConverterSpy();
		factoredToFedoraConverters.add(converter);

		return converter;
	}

}
