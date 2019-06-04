package edn.cloud.sfactor.business.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import edn.cloud.business.api.util.UtilBuilderFunctions;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.sfactor.business.connectivity.HttpConnectorSuccessFactor;
import edn.cloud.sfactor.persistence.dao.AdminParametersDAO;
import edn.cloud.sfactor.persistence.dao.EventListenerDAO;
import edn.cloud.sfactor.persistence.dao.FieldsMappingPpdDAO;
import edn.cloud.sfactor.persistence.dao.FieldsTemplateLibraryDAO;
import edn.cloud.sfactor.persistence.dao.SfEntityDAO;
import edn.cloud.sfactor.persistence.dao.StructureBusinessDAO;
import edn.cloud.sfactor.persistence.entities.SfEntity;
import edn.cloud.sfactor.persistence.entities.SfNavProperty;
import edn.cloud.sfactor.persistence.entities.SfProperty;

public class DataBaseBuilder {

	private static DataBaseBuilder INSTANCE = null;
	private final UtilLogger logger = UtilLogger.getInstance();

	public static synchronized DataBaseBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DataBaseBuilder();
		}
		return INSTANCE;
	}

	public void reset() {
		SfEntityDAO entityDoa = new SfEntityDAO();
		entityDoa.reset();
	}

	public void init() {
		logger.info(QueryBuilder.class, "Create the iteration arround SF Objects");

		// empInfo_of_user

		try {
			String joe = HttpConnectorSuccessFactor.getInstance().getMetadata();
			InputStream stream = new ByteArrayInputStream(joe.getBytes("UTF-8"));
			SAXBuilder sxb = new SAXBuilder();
			Document document = new Document();
			try {
				document = sxb.build(stream);
			} catch (Exception e) {
				logger.error(QueryBuilder.class, "error");
			}

			Element racine = document.getRootElement();
			Element DataServices = racine.getChild("DataServices", Namespace.getNamespace("edmx", "http://schemas.microsoft.com/ado/2007/06/edmx"));
			Namespace ns = Namespace.getNamespace("SFOData", "http://schemas.microsoft.com/ado/2008/09/edm");
			List<Element> listSchemas = DataServices.getChildren("Schema", ns);

			Iterator<Element> i = listSchemas.iterator();
			while (i.hasNext()) {
				Element courant = (Element) i.next();
				getEntities(courant, ns, document);
			}

		} catch (IOException | InvalidResponseException ex) {
			logger.error(QueryBuilder.class, "error");
		}

	}

	public void initData() {
		// delete all
		AdminParametersDAO daoA = new AdminParametersDAO();
		EventListenerDAO daoE = new EventListenerDAO();
		StructureBusinessDAO daoS = new StructureBusinessDAO();
		FieldsMappingPpdDAO daoF = new FieldsMappingPpdDAO();
		FieldsTemplateLibraryDAO daoLib = new FieldsTemplateLibraryDAO();

		try {
			daoLib.deleteAll();
		} catch (Exception e) {
			e.getStackTrace();
		}

		try {
			daoE.deleteAll();
		} catch (Exception e) {
			e.getStackTrace();
		}

		try {
			daoS.deleteAllStructure();
		} catch (Exception e) {
			e.getStackTrace();
		}

		try {
			daoF.deleteAll();
		} catch (Exception e) {
			e.getStackTrace();
		}

		try {
			daoA.deleteAll();
		} catch (Exception e) {
			e.getStackTrace();
		}

	}

	private void getEntities(Element racine, Namespace ns, Document doc) {
		List<Element> listEntities = racine.getChildren("EntityType", ns);
		Iterator<Element> i = listEntities.iterator();
		while (i.hasNext()) {
			Element entType = (Element) i.next();

			// ADD TO DATABASE SfEntity;

			SfEntityDAO entityDoa = new SfEntityDAO();
			SfEntity entityVal = new SfEntity();
			entityVal.setName(entType.getAttributeValue("Name"));

			entityVal.setPrimkey(entType.getChild("Key", ns).getChild("PropertyRef", ns).getAttributeValue("Name"));

			List<Element> listProps = entType.getChildren("Property", ns);

			Iterator<Element> prop = listProps.iterator();
			while (prop.hasNext()) {
				Element entProp = (Element) prop.next();

				// ADD TO DATABASE SfProperty;
				SfProperty entityProp = new SfProperty();
				entityProp.setName(UtilBuilderFunctions.normalizeStrintAscii(entProp.getAttributeValue("Name")));
				entityProp.setLabel(UtilBuilderFunctions.normalizeStrintAscii(entProp.getAttributeValue("label", Namespace.getNamespace("sap", "http://www.successfactors.com/edm/sap"))));
				entityProp.setType(entProp.getAttributeValue("Type"));

				entityVal.addProperties(entityProp);

			}

			List<Element> listNavProps = entType.getChildren("NavigationProperty", ns);

			Iterator<Element> navprop = listNavProps.iterator();

			while (navprop.hasNext()) {
				Element entNavProp = (Element) navprop.next();

				// ADD TO DATABASE SfNavProperty;
				SfNavProperty entityNavProp = new SfNavProperty();
				entityNavProp.setName(entNavProp.getAttributeValue("Name"));

				String[] partsRel = entNavProp.getAttributeValue("Relationship").split("\\.");

				entityNavProp.setRelationship(partsRel[1]);

				entityNavProp.setLabel(entNavProp.getAttributeValue("label", Namespace.getNamespace("sap", "http://www.successfactors.com/edm/sap")));

				entityVal.addNavproperties(entityNavProp);

				String query = "//SFOData:Association[@Name='" + entityNavProp.getRelationship() + "']//SFOData:End[@Role='" + entNavProp.getAttributeValue("ToRole") + "']";

				XPathExpression<Element> xpe = XPathFactory.instance().compile(query, Filters.element(), null, Namespace.getNamespace("SFOData", "http://schemas.microsoft.com/ado/2008/09/edm"));
				Element urlv = xpe.evaluateFirst(racine);

				entityNavProp.setType(urlv.getAttributeValue("Type").substring(8));
				entityNavProp.setMultiplicity(urlv.getAttributeValue("Multiplicity"));

			}

			entityDoa.save(entityVal);
			// break;
		}

	}
}
