package wdframework.common;


import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.saucelabs.saucerest.SauceREST;

import wdframework.driver.BrowserType;
import wdframework.driver.Driver;
import wdframework.driver.DriverType;
import wdframework.driver.TestConfig;
import wdframework.logger.Logger;

/**
 * Common super class for driver running 
 * @author Eldo Rajan
 *
 */
public class DriverRunner extends Driver{


	DriverType mode=null;BrowserType browser=null;String baseUrl=null;String hubUrl =null;
	// Separate driver instance for each thread
	protected ThreadLocal<WebDriver> localdriver = new ThreadLocal<WebDriver>();
	protected ThreadLocal<RemoteWebDriver> remotedriver = new ThreadLocal<RemoteWebDriver>();
	protected ThreadLocal<String> sessionId = new ThreadLocal<String>();
	TestConfig testconfig = null;   

	/**
	 * Before Method instantiations
	 * @param theTestContext
	 * @param theTestResult
	 */
	@BeforeMethod(alwaysRun = true)
	public void startBrowser(ITestContext theTestContext) {
		testconfig = new TestConfig();
		String suiteName = theTestContext.getSuite().getXmlSuite().getName();
		try {

			browser = BrowserType.getBrowserType(System.getProperty("browser"));
			mode = DriverType.getDriverType(System.getProperty("mode"));
			baseUrl = System.getProperty("baseurl");
			hubUrl = System.getProperty("huburl");
			
			if(browser==null){
				browser = BrowserType.getBrowserType(theTestContext.getCurrentXmlTest().getParameter("browser"));            	
			}
			if(mode==null){
				mode = DriverType.getDriverType(theTestContext.getCurrentXmlTest().getParameter("mode"));
			}
			if(baseUrl==null){
				baseUrl = theTestContext.getCurrentXmlTest().getParameter("baseurl");	
			}
			if(hubUrl==null){
				hubUrl = theTestContext.getCurrentXmlTest().getParameter("huburl");	
			}


			if(browser!=null){
				testconfig.setBrowser(browser);
			}
			if(mode!=null){
				testconfig.setMode(mode);
			}
			if(baseUrl!=null){
				testconfig.setBaseUrl(baseUrl);
			}
			if(hubUrl!=null){
				testconfig.setHubUrl(hubUrl);
			}

			
			
			String ieDriver = testconfig.getIEDriver();
			String chromeDriver = testconfig.getChromeDriver();
			String ghostDriver = testconfig.getPhantomJsDriver();
			
			browser = testconfig.getBrowser();
			mode = testconfig.getMode();
			hubUrl = testconfig.getHubUrl();
			baseUrl = testconfig.getBaseUrl();

			switch (mode) {
			case Local: {            
				localdriver.set(browserProfileConfiguration(browser, ieDriver, chromeDriver, ghostDriver));
				break;
			}
			case Grid: {
				remotedriver.set(browserProfileConfigurationRemote(browser, hubUrl, ghostDriver));
				break;
			}
			case Cloud: {
				remotedriver.set(browserProfileConfigurationCloud(browser, hubUrl, suiteName));
				sessionId.set(((RemoteWebDriver) getWebDriver()).getSessionId().toString());
				break;
			}

			default:           
				try {
					Logger.error(
							"Fail to intialize the driver, please check mode parameter.");
					throw new Exception(
							"Fail to intialize the driver, please check mode parameter.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			getWebDriver().get(baseUrl);
			getWebDriver().manage().window().maximize();
			getWebDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);            
		} catch (Exception e) {
			e.printStackTrace();
			/*throw new RuntimeException("Exception during browser startup: ",
                    e);*/
		}


	}

	/**
	 * After method disembodifying 
	 */
	@AfterMethod(alwaysRun = true)
	public void stopBrowser() {
		Logger.info("Closing the browser session after the method gets executed in progress");
		if (localdriver.get() != null) {
			getWebDriver().quit();
			localdriver.remove();
		}else if (remotedriver.get() != null) {
			getWebDriver().quit();
			remotedriver.remove();
			switch (mode) {
			case Local: {            
				break;
			}
			case Grid: {
				break;
			}
			case Cloud: {
				ITestResult testStatus = Reporter.getCurrentTestResult();
				SauceREST client = new SauceREST(hubUrl.split(":")[0],hubUrl.split(":")[1]);

				if (testStatus.isSuccess()) {
					client.jobPassed(getSessionId());
				} else {
					client.jobFailed(getSessionId());
				}

				break;
			}
			default:           
				break;
			}
		}else{
			getWebDriver().quit();
		}

		Logger.info("Closed the browser session after the method gets executed");
	}

	/**
	 * gets webdriver instance   
	 * @return
	 */
	public WebDriver getWebDriver(){
		WebDriver driver=null;
		switch (mode) {
		case Local: {            
			driver = localdriver.get();
			break;
		}
		case Grid: {
			driver = remotedriver.get();
			break;
		}
		case Cloud: {
			driver = remotedriver.get();
			break;
		}

		default:           
			try {
				Logger.error(
						"Fail to intialize the driver, please check mode parameter.");
				throw new Exception(
						"Fail to intialize the driver, please check mode parameter.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return driver;

	}

	/**
	 * gets session id   
	 * @return
	 */
	public String getSessionId(){
		String jobId=null;
		try {
			jobId = sessionId.get();	

		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(
					"Fail to intialize the session, please session parameter.");
		}
		return jobId;

	}

}
