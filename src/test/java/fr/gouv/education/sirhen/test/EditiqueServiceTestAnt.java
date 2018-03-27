package fr.gouv.education.sirhen.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.gouv.education.sirhen.ct.editique.service.IEditiqueService;

/**
 * Classe de test du service de génération de documents hors arrêtés.
 *
 */
@ContextConfiguration(locations = {"classpath*:spring/ct-editique-junit-context.xml",
	"classpath*:spring/ct-editique-test-context.xml" })
@RunWith(value = SpringJUnit4ClassRunner.class)
public class EditiqueServiceTestAnt {

	/**
	 * L'instance du service.
	 */
	@Resource(name = "ed.service")
	private IEditiqueService service;

	/**
	 * Test de génération de document.
	 */
	@Test
	public final void genererDocument() {

		try {
			final String fluxXml = creerFluxXML();
			final byte[] document = service.genererDocument("entretienProfessionnel_crEvaluation", fluxXml);
			assertNotNull("Le document doit être non null", document);
			File file = new File("c:/tmp/test2.pdf");
			if (file.exists()) {
				file.delete();
			}
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.write(document);
			}
		} catch (final Exception e) {
			fail(e.getMessage());
		}

	}

	/**
	 * Convertit un objet org.w3c.dom.Document object en objet com.adobe.idp.Document.
	 *
	 * @throws TransformerException
	 *             Exception
	 * @return String le xml
	 * @throws ParserConfigurationException
	 *             Exception technique
	 * @throws TransformerException
	 *             Exception technique
	 */
	private String creerFluxXML() throws TransformerException, ParserConfigurationException {

		// ########Construction du fichier XML###########################
		// Create DocumentBuilderFactory and DocumentBuilder objects
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();

		// Create a new Document object
		final Document xml = builder.newDocument();

		// racine
		final Element personne = xml.createElement("personne");
		xml.appendChild(personne);

		// premiers niveaux
		final Element nom = xml.createElement("nom");
		nom.appendChild(xml.createTextNode("Sarko"));
		personne.appendChild(nom);

		final Element prenom = xml.createElement("prenom");
		prenom.appendChild(xml.createTextNode("Nico"));
		personne.appendChild(prenom);

		// Création du transformeur java
		final TransformerFactory transFact = TransformerFactory.newInstance();
		final Transformer transForm = transFact.newTransformer();

		// Création du flux do sortie "ByteArrayOutputStream"
		final ByteArrayOutputStream myOutStream = new ByteArrayOutputStream();

		// Création du flux d'entrée
		final javax.xml.transform.dom.DOMSource myInput = new DOMSource(xml);

		// Transformation
		final javax.xml.transform.stream.StreamResult myOutput = new StreamResult(myOutStream);
		transForm.transform(myInput, myOutput);

		return myOutput.getOutputStream().toString();
	}

}
