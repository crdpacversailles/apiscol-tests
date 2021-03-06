package fr.ac_versailles.crdp.apiscol.tests.gp2;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.xml.XmlPage;

import fr.ac_versailles.crdp.apiscol.tests.ApiScolTests;

//@Ignore
public class ContentAndMetaUbzTest extends ApiScolTests {
	@Before
	public void initialize() {
		createClient();
	}

	@After
	public void close() {
		closeClient();
	}

	@Test
	public void testPostingUbzDocumentWithMetadata() {
		URL url = getServiceUrl("/edit/meta", editionServiceBaseUrl);
		assertTrue("The Url must be valid", url != null);
		assertTrue(
				"An authorization token was not gotten with this credentials",
				getAuthorizationToken(url, LOGIN, PASSWORD));
		XmlPage page = postMetadataDocument("cuisson1.xml", url);
		String metadataLinkLocation = getAtomLinkLocation(page, "self",
				"text/html");
		XmlPage newResourcePage = getNewResourcePage(metadataLinkLocation);
		String eTag = getAtomUpdatedField(newResourcePage);
		String urn = getAtomId(newResourcePage);
		String editUri = getEditMediaUri(newResourcePage);
		XmlPage page2 = postFileDocument(editUri, urn,
				"Mode de cuisson sauter Cuisson sauter-01. Le contexte.ubz", eTag);
		XmlPage page3 = askForResourceRepresentation(metadataLinkLocation);
		XmlPage page4 = getThumbsSuggestionForMetaId(metadataLinkLocation);
		String firstThumbSuggestionUri = getFirstThumbSuggestionUri(page4);
		assertTrue("The first thumb suggestion may not be empty for metadata "
				+ metadataLinkLocation,
				StringUtils.isNotEmpty(firstThumbSuggestionUri));
		XmlPage page5 = chooseThumbForMetadataId(metadataLinkLocation,
				firstThumbSuggestionUri, getThumbEtag(page4));
		String thumbUri = testDefaultThumbHasuri(page5);
		XmlPage page6 = getThumbForMetadataId(metadataLinkLocation);
		testThumbUriIs(page6, thumbUri, metadataLinkLocation);

		// deleteResource(page);
	}

}
