import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.nio.file.Path
import java.nio.file.Paths

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

import org.w3c.dom.Document

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.testobject.HttpBodyContent
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

ResponseObject response = WS.sendRequest(findTestObject('Yahoo.com RSS mostviewed'))

if (response.isXmlContentType()) {
	println "is XML"
	HttpBodyContent content = response.getBodyContent()
	println "content encoding: ${content.getContentEncoding()}"
	println "content length: ${content.getContentLength()}"
	println "content type: ${content.getContentType()}"
	InputStream is = content.getInputStream()
	
	// parsing XML document
	DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance()
	dbfactory.setNamespaceAware(true)
	DocumentBuilder db = dbfactory.newDocumentBuilder()
	Document document = db.parse(is)
	
	// get access to Items using XPath
	XPath xpath = XPathFactory.newInstance().newXPath()
	WebUI.openBrowser('')
	Path outdir = Paths.get(RunConfiguration.getProjectDir()).resolve('tmp')
	for (int x = 1; x <= 3; x++) {
		String url = (String)xpath.evaluate(
			"/rss/channel/item[${x}]/link",
			document, XPathConstants.STRING)
		println "url: ${url}"
		if (url != null) {
			// taking evidence screenshot
			WebUI.navigateToUrl(url)
			WebUI.delay(2)
			Path outfile = outdir.resolve(URLEncoder.encode(url,'UTF-8') + ".png")
			WebUI.takeScreenshot(outfile.toString())
		}
	}
	
	WebUI.closeBrowser()
}