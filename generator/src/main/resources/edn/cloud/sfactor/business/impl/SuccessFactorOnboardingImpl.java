package edn.cloud.sfactor.business.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import edn.cloud.business.api.util.UtilCodesEnum;
import edn.cloud.business.api.util.UtilDateTimeAdapter;
import edn.cloud.business.api.util.UtilLogger;
import edn.cloud.business.api.util.UtilMapping;
import edn.cloud.business.connectivity.http.InvalidResponseException;
import edn.cloud.business.dto.ContentFileInfo;
import edn.cloud.business.dto.PostInfo;
import edn.cloud.business.dto.ResponseGenericDto;
import edn.cloud.business.dto.SimpleHttpResponse;
import edn.cloud.business.dto.integration.SlugItem;
import edn.cloud.ppdoc.business.impl.PpdGeneratorImpl;
import edn.cloud.ppdoc.business.interfaces.PpdGenerator;
import edn.cloud.sfactor.business.connectivity.HttpConnectorSuccessFactor;
import edn.cloud.sfactor.business.connectivity.HttpConnectorSuccessFactorOnb;
import edn.cloud.sfactor.business.connectivity.HttpConnectorSuccessFactorOnbV2;
import edn.cloud.sfactor.business.interfaces.SucessFactorOnboarding;

public class SuccessFactorOnboardingImpl implements SucessFactorOnboarding {
	private UtilLogger loggerSingle = UtilLogger.getInstance();

	/**
	 * get kms user of user in onboarding module version 2
	 * 
	 * @param String
	 *            userId
	 * @param String
	 *            kms
	 */
	public String getUserInfoOnboardingModuleV2(String userId) {
		try {
			String st3 = HttpConnectorSuccessFactor.getInstance().executeGET("OnboardingCandidateInfo?$select=kmsUserId&$filter=userId%20eq%20%27" + userId + "%27");

			JSONObject candidateRetrieve = new JSONObject(st3);
			JSONArray usersAsCandidate = candidateRetrieve.getJSONObject("d").getJSONArray("results");
			JSONObject candidateFinal = (JSONObject) usersAsCandidate.get(0);

			try {
				return candidateFinal.getString("kmsUserId");
			} catch (Exception e) {
				loggerSingle.info(e.getMessage());
				return null;
			}

		} catch (Exception e) {
			// TODO: handle exception
			loggerSingle.info(e.getMessage());
			return null;
		}
	}

	/**
	 * get attach content for all modules
	 * 
	 * @param String
	 *            idDocumentTypeOnb
	 * @param String[]
	 *            patternNameOB [0]=begin position in all string [1]=separator [2]=
	 *            position of element [3] = begin position of namefile
	 * @param String
	 *            kmsId
	 * @param Boolean
	 *            loadContent
	 * 
	 * @return ArrayList<ContentFileInfo>
	 */
	public ArrayList<ContentFileInfo> getAttachNameFromOnboardingV2(String idDocumentTypeOnb, String[] patternNameOB, String kmsId, Boolean loadContent) {
		ArrayList<ContentFileInfo> returnList = new ArrayList<ContentFileInfo>();
		ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();
		;

		try {
			if (idDocumentTypeOnb != null) {
				String sessionONB = HttpConnectorSuccessFactorOnbV2.getInstance().getSessionONB();

				if (sessionONB.equals("OK")) {
					loggerSingle.info(sessionONB);
					loggerSingle.info(idDocumentTypeOnb);
					loggerSingle.info(kmsId);

					try {
						Charset CP866 = Charset.forName("CP866");

						SimpleHttpResponse st = HttpConnectorSuccessFactorOnbV2.getInstance().getAttachONBV2(idDocumentTypeOnb, kmsId);
						ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(st.getContent().getBytes(CP866)), CP866);
						ZipEntry ze;

						while ((ze = zis.getNextEntry()) != null) {
							if (ze.getName() != null && patternNameOB != null) {
								bOutStream = new ByteArrayOutputStream();
								ContentFileInfo file = new ContentFileInfo();
								file.setRef1(new SlugItem());

								// name file
								file.setFileName(ze.getName().substring(Integer.valueOf(patternNameOB[3].toString()), ze.getName().length()));
								
								// load document type from name of file
								file.getRef1().setCode(UtilCodesEnum.CODE_DEFAULT.getCode());
								if (patternNameOB != null) {
									String nameFile = UtilMapping.loadStringFromPattern(ze.getName(), patternNameOB[0].toString(), "", Integer.valueOf(patternNameOB[2].toString()));
									String nameDocumentType = UtilMapping.loadStringFromPattern(ze.getName(), patternNameOB[0].toString(), patternNameOB[1], Integer.valueOf(patternNameOB[2].toString()));
									
									if (nameDocumentType != null) {	
										file.getRef1().setCode(nameDocumentType);
									}
									
									if (nameFile != null) {	
										file.setFileName(nameFile);
									}
								}
								
								file.getRef1().setId(UtilMapping.toSlug(kmsId + "_" + file.getFileName()));// External id

								if (loadContent) {
									byte[] Unzipbuffer = new byte[1];
									int Unziplength = 0;
									while ((Unziplength = zis.read(Unzipbuffer)) > 0) {
										bOutStream.write(Unzipbuffer, 0, Unziplength);
									}

									zis.closeEntry();
									bOutStream.close();
									file.setFile(bOutStream.toByteArray());
								}

								returnList.add(file);
							}
						}

						zis.close();

					} catch (IOException e) {
						e.printStackTrace();
						bOutStream.close();
					}
				} else {
					loggerSingle.info("NOT OK");
				}
			}

			return returnList;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	/**
	 * get attach content for all modules
	 * 
	 * @param String
	 *            idDocumentTypeOnb
	 * @param String[]
	 *            patternNameOB [0]=begin position in all string [1]=separator [2]=
	 *            position of element [3] = begin position of namefile
	 * @param String
	 *            kmsId
	 * @param Boolean
	 *            loadContent
	 * 
	 * @return ArrayList<ContentFileInfo>
	 */
	public ArrayList<ContentFileInfo> getAttachNameFromOnboardingV2Key(String idDocumentTypeOnb, String[] patternNameOB, String kmsId, Boolean loadContent, String key) {
		ArrayList<ContentFileInfo> returnList = new ArrayList<ContentFileInfo>();
		ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();
		;

		try {
			if (idDocumentTypeOnb != null) {
				String sessionONB = HttpConnectorSuccessFactorOnbV2.getInstance().getSessionONB();

				if (sessionONB.equals("OK")) {
					loggerSingle.info(sessionONB);
					loggerSingle.info(idDocumentTypeOnb);
					loggerSingle.info(kmsId);

					try {
						Charset CP866 = Charset.forName("CP866");

						SimpleHttpResponse st = HttpConnectorSuccessFactorOnbV2.getInstance().getAttachONBV2Key(idDocumentTypeOnb, kmsId, key);
						ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(st.getContent().getBytes(CP866)), CP866);
						ZipEntry ze;

						while ((ze = zis.getNextEntry()) != null) {
							if (ze.getName() != null && patternNameOB != null) {
								bOutStream = new ByteArrayOutputStream();
								ContentFileInfo file = new ContentFileInfo();
								file.setRef1(new SlugItem());

								// name file
								file.setFileName(ze.getName().substring(Integer.valueOf(patternNameOB[3].toString()), ze.getName().length()));

								// load document type from name of file
								file.getRef1().setCode(UtilCodesEnum.CODE_DEFAULT.getCode());
								if (patternNameOB != null) {
									String nameFile = UtilMapping.loadStringFromPattern(ze.getName(), patternNameOB[0].toString(), "", Integer.valueOf(patternNameOB[2].toString()));
									String nameDocumentType = UtilMapping.loadStringFromPattern(ze.getName(), patternNameOB[0].toString(), patternNameOB[1], Integer.valueOf(patternNameOB[2].toString()));

									if (nameDocumentType != null) {	
										file.getRef1().setCode(nameDocumentType);
									}
									
									if (nameFile != null) {	
										file.setFileName(nameFile);
									}
								}
								
								file.getRef1().setId(UtilMapping.toSlug(kmsId + "_" + file.getFileName()));// External id

								if (loadContent) {
									byte[] Unzipbuffer = new byte[1];
									int Unziplength = 0;
									while ((Unziplength = zis.read(Unzipbuffer)) > 0) {
										bOutStream.write(Unzipbuffer, 0, Unziplength);
									}

									zis.closeEntry();
									bOutStream.close();
									file.setFile(bOutStream.toByteArray());
								}

								returnList.add(file);
							}
						}

						zis.close();

					} catch (IOException e) {
						e.printStackTrace();
						bOutStream.close();
					}
				} else {
					loggerSingle.info("NOT OK");
				}
			}

			return returnList;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}

	public ResponseGenericDto test(String idDocumentTypeOnb, String kmsId, Boolean loadContent) {
		ArrayList<ContentFileInfo> returnList = new ArrayList<ContentFileInfo>();
		ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();
		ResponseGenericDto tst = new ResponseGenericDto();

		try {
			if (idDocumentTypeOnb != null) {
				String sessionONB = HttpConnectorSuccessFactorOnbV2.getInstance().getSessionONB();

				if (sessionONB.equals("OK")) {
					loggerSingle.info(sessionONB);
					loggerSingle.info(idDocumentTypeOnb);
					loggerSingle.info(kmsId);

					ContentFileInfo file = new ContentFileInfo();

					try {
						Charset CP866 = Charset.forName("CP866");

						SimpleHttpResponse st = HttpConnectorSuccessFactorOnbV2.getInstance().getAttachONBV2(idDocumentTypeOnb, kmsId);
						ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(st.getContent().getBytes(CP866)), CP866);
						ZipEntry ze;

						while ((ze = zis.getNextEntry()) != null) {
							file.setFileName(kmsId + "_" + ze.getName());

							if (loadContent) {
								byte[] Unzipbuffer = new byte[1];
								int Unziplength = 0;
								while ((Unziplength = zis.read(Unzipbuffer)) > 0) {
									bOutStream.write(Unzipbuffer, 0, Unziplength);
								}

								zis.closeEntry();
								bOutStream.close();
							}
						}

						zis.close();

						Gson gson = new Gson();
						PostInfo post = new PostInfo();
						post.setExternal_unique_id("");
						post.setTitle(idDocumentTypeOnb + "_" + kmsId);
						post.setDate(UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), new Date()));
						post.setEmployee_technical_id("9309730");
						post.setDocument_type_code("401k-loan-documents");
						String[] orga = { "USA" };
						post.setOrganization_codes(orga);

						// Metadata
						HashMap<String, String> map = new HashMap<>();
						map.put("country", "Argentina (ARG)");
						map.put("bsc-date", UtilDateTimeAdapter.getDateFormat(UtilCodesEnum.CODE_FORMAT_DATE.getCode(), new Date()));
						post.setDocument_type_metas(map);

						ContentFileInfo file1 = new ContentFileInfo();
						file1 = new ContentFileInfo();
						file1.setFile(bOutStream.toByteArray());

						PpdGenerator ppdGenerator = new PpdGeneratorImpl();
						// tst = ppdGenerator.wServiceSetCompanyDocument("X-KEY", file1,
						// gson.toJson(post));
						tst.setMessage(tst.getMessage() + " " + UtilMapping.toStringHtmlFormat(gson.toJson(post)));

						if (loadContent)
							file.setFile(bOutStream.toByteArray());

					} catch (IOException e) {
						e.printStackTrace();
						bOutStream.close();
					}

				} else {
					loggerSingle.info("NOT OK");
				}
			}

			return tst;
		} catch (IOException | InvalidResponseException ex) {
			loggerSingle.error(this, "Error " + ex.toString());
			return null;
		}
	}
}
