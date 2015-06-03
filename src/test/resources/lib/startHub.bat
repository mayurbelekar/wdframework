start java -jar selenium-server-standalone-2.41.0.jar -role hub -port 4444
start java -jar selenium-server-standalone-2.41.0.jar -port 5555 -role node -hub http://localhost:4444/grid/register -browser browserName=firefox,maxInstances=5 platform=WINDOWS, ensureCleanSession=true, javascriptEnabled=true, acceptSslCerts=true, ignoreProtectedModeSettings=true, ignoreZoomSetting=true, takesScreenshot=true
start java -jar selenium-server-standalone-2.41.0.jar -port 5556 -role node -hub http://localhost:4444/grid/register -browser browserName=chrome,maxInstances=5 platform=WINDOWS, ensureCleanSession=true, javascriptEnabled=true, acceptSslCerts=true, ignoreProtectedModeSettings=true, ignoreZoomSetting=true, takesScreenshot=true -Dwebdriver.chrome.driver="chromedriver.exe"
start java -jar selenium-server-standalone-2.41.0.jar -port 5557 -role node -hub http://localhost:4444/grid/register -browser browserName="internet explorer",version=11 platform=WINDOWS, ensureCleanSession=true, javascriptEnabled=true, acceptSslCerts=true, ignoreProtectedModeSettings=true, ignoreZoomSetting=true, takesScreenshot=true -Dwebdriver.ie.driver="IEDriverServer.exe"
start java -jar selenium-server-standalone-2.41.0.jar -port 5558 -role node -hub http://localhost:4444/grid/register -browser browserName=safari,maxInstances=1 platform=WINDOWS, ensureCleanSession=true, javascriptEnabled=true, acceptSslCerts=true, ignoreProtectedModeSettings=true, ignoreZoomSetting=true, takesScreenshot=true
start java -jar selenium-server-standalone-2.41.0.jar -port 5559 -role node -hub http://localhost:4444/grid/register -browser browserName=opera,maxInstances=5 platform=WINDOWS, ensureCleanSession=true, javascriptEnabled=true, acceptSslCerts=true, ignoreProtectedModeSettings=true, ignoreZoomSetting=true, takesScreenshot=true
setx path "%path%;phantomjs.exe"
start phantomjs --webdriver=9019 phantomjs.binary.path=phantomjs.exe --webdriver-selenium-grid-hub=http://127.0.0.1:4444
