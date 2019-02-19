package se.uu.ub.cora.diva.tocorastorage.fedora;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.httphandler.HttpHandler;

public class HttpHandlerSpy implements HttpHandler {

	public String requestMetod;
	public String responseText;
	public int responseCode;
	public Map<String, String> requestProperties = new HashMap<String, String>();
	public List<String> outputStrings = new ArrayList<String>();

	@Override
	public void setRequestMethod(String requestMetod) {
		this.requestMetod = requestMetod;
	}

	@Override
	public String getResponseText() {
		return responseText;
	}

	@Override
	public int getResponseCode() {
		return responseCode;
	}

	@Override
	public void setOutput(String outputString) {
		outputStrings.add(outputString);
	}

	@Override
	public void setRequestProperty(String key, String value) {
		requestProperties.put(key, value);
	}

	@Override
	public String getErrorText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStreamOutput(InputStream stream) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getHeaderField(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
