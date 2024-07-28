#!/bin/bash
echo "Building java code ....."
javac -d out -sourcepath src src/utp/utils/siteblocker/SiteBlocker.java     
javac -d out -sourcepath src src/utp/utils/siteblocker/SiteBlockerGUI.java 
jar cfm SiteBlocker.jar MANIFEST.MF -C out . 
echo "Done"
