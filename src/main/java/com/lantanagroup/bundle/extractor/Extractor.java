package com.lantanagroup.bundle.extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.JsonParser;
import ca.uhn.fhir.parser.XmlParser;

public class Extractor {
	
	private static FhirContext ctxR4 = FhirContext.forR4();
	private static JsonParser jp = (JsonParser)ctxR4.newJsonParser();
	private static XmlParser xp = (XmlParser)ctxR4.newXmlParser();
	private File bundleFile;
	private File outputFolder;
	private boolean xml = true;

	public static void main(String[] args) {
		System.out.println("args[0] = " + args[0]);
		File bundle = new File(args[0]);
		File outputFolder = null;
		if (args.length > 1) {
			outputFolder = new File(args[1]);
		}
		new Extractor(bundle,outputFolder);
	}
	
	
	public Extractor(File bundleFile, File outputFolder) {
		this.bundleFile = bundleFile;
		if (outputFolder == null) {
			this.outputFolder = bundleFile.getParentFile();
		} else {
			this.outputFolder = outputFolder;
		}
		try {
			extractBundle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void extractBundle() throws DataFormatException, IOException {
		System.out.println("Extracting " + bundleFile.getAbsolutePath());
		Bundle b = null;
		BufferedReader buffy = new BufferedReader(new FileReader(bundleFile));
		if (bundleFile.getName().toLowerCase().endsWith(".json")) {
			b = jp.doParseResource(Bundle.class, buffy);
		} else {
			b = xp.doParseResource(Bundle.class, buffy);
		}
		List<BundleEntryComponent> entries = b.getEntry();
		for (BundleEntryComponent entry : entries) {
			Resource res = entry.getResource();
			File f = new File(outputFolder, res.getIdPart() + "xml");
			System.out.println(" - " + f.getAbsolutePath());
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			xp.encodeResourceToWriter(res, writer);
		}
	}
	
}
