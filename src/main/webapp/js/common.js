
// Define the browser used - Added by Ismail
   NS4 = (document.layers);
   IE4 = (document.all);
  ver4 = (NS4 || IE4);   
 isMac = (navigator.appVersion.indexOf("Mac") != -1);
isMenu = (NS4 || (IE4 && !isMac));

/**
 * refresh window opener
 */
function refreshOpenerWindow()
{
    //alert('about to refresh opener: '+window.opener.document.location);
    
    if (window.opener)
    {
        window.opener.document.location = window.opener.document.location;
    }
    else
    {
        // in case the window is lost
        alert('Opener window cannot be found....!');
    }
}

function closeWindow()
{
    window.close();
}


/**
 * refresh window opener
 */
function refreshOpenerWindow()
{
	//alert('about to refresh opener: '+window.opener.document.location);
	
	if (window.opener)
	{
		window.opener.document.location = window.opener.document.location;
	}
	else
	{
		// in case the window is lost
		alert('Opener window cannot be found....!');
	}
}

function closeWindow()
{
	window.close();
}



// Open a custom javascript window
function fnNewWin(name,url,width,height) 
{
	var left = (screen.width - width)/2;
	var top = (screen.height - height - 100 )/2;

	// if IE, open the custom frame window
	if (IE4) 
	{
		window.open(url,name,'scrollbars=yes,width='+width+',height='+height+',left='+left+',top='+top+',resizable=yes,toolbar=yes');
	}

	// if Netscape, do directly to file
	// Note; generally, netscape does not support embedded active x content
	else 
	{
		window.open(url,name,'width='+width+',height='+height+',left='+left+',top='+top+',scrollable=yes,resizable=yes,maximize=1,status=yes,toolbar=yes');
	}
}

// Open a custom javascript window
function fnNewWinLookup(name,url,width,height,form,field) 
{
	var left = (screen.width - width)/2;
	var top = (screen.height - height - 100 )/2;

	// if IE, open the custom frame window
	if (IE4) 
	{
		window.open(url,name,'width='+width+',height='+height+',left='+left+',top='+top+',scrollbars=yes,resizable=yes,maximize=1,status=yes,toolbar=yes');
	}

	// if Netscape, do directly to file
	// Note; generally, netscape does not support embedded active x content
	else 
	{
		window.open(url,name,'width='+width+',height='+height+',left='+left+',top='+top+',scrollbars=yes,resizable=yes,maximize=1,status=yes,toolbar=yes');
	}
}

function fnOpenWindow(url, title)
{
	
    //alert(url);    
    var newWin = window.open(url, title,"menubar=1,toolbar=1,scrollbars=1,status=yes,resizable=1,width=700,height=700");

    if (window.focus) {newWin.focus()}
    {
    	return false;
    }
}

function fnOpenWindow2(url, title, w, h)
{

	var left = (screen.width - w)/2;
	var top = (screen.height - h - 100 )/2;
	
    var newWin = window.open(url, title, 'menubar=1, toolbar=1, scrollbars=1, status=yes, resizable=1, width='+w+',height='+h+',left='+left+',top='+top);

    if (window.focus) {newWin.focus()}
    {
    	return false;
    }
}

function showOrHideItem(obj)
{
	var elem = document.getElementById(obj);
	if (eval(elem)) 
	{				
		if (elem.style.visibility == 'visible')		
			elem.style.visibility = 'hidden';		
		else		
			elem.style.visibility = 'visible';			
	}
}

function fnConfirm(url, msg)
{
    if (window.confirm('Are you sure to ' + msg + ' ?'))
    {
        document.location = url;
    }
}	



function fnRedirect(url, millis)
{
    var t = setTimeout("document.location='" + url + "'", millis);    
}


/**
 * Opens a link in a target window; using javascript
 * @param url
 * @returns
 */
function fnRedirect(url, target) 
{
	var a = document.createElement('a');
	a.target=target;
	a.href=url;
	a.click();
}

/**
 * Opens a link in a target window; using javascript
 * @param url
 * @returns
 */
function fnRedirect_blank(url) 
{
	var a = document.createElement('a');
	a.target="_blank";
	a.href=url;
	a.click();
}

function fnEncode(url)
{
	return encodeURIComponent(url);
}

/**
 * Toggle displaying a div on/off
 * @returns
 */
function toggleDisplay(divID) 
{
    var ele = document.getElementById(divID);
    if (ele)
    {    	    
	    if(ele.style.display == "block") 
	        ele.style.display = "none";
	    else 
	        ele.style.display = "block";
    }
    else
    {
    	alert('toggleDisplay element [' + divID + '] does not exists!');
    }
}


function getRandomInt(max) 
{
	return Math.floor(Math.random() * Math.floor(max));
}

function fnScrollTextarea(id)
{
	var textarea = document.getElementById(id);
	if (textarea)
		textarea.scrollTop = textarea.scrollHeight;
}


 	