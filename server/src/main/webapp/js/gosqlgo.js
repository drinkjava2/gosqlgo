//json example: {"code":200, "msg":"sucess", data:"some data", debugInfo:"Error:xxx"}

//Below methods return JSON object 
function $$gsg(text) {
	  if(text==null || text=="" || text.indexOf("FULL ")==0) 
		  return "";
	  return getGsgJson("", text, arguments);
}

function $$java(text) {
	return getGsgJson("java", text, arguments);
}

function $$javaTx(text) {
	return getGsgJson("javaTx", text, arguments);
}

function $$qryObject(text) {
	return getGsgJson("qryObject", text, arguments);
}
  
function $$qryArray(text) {
	return getGsgJson("qryArray", text, arguments);
}

function $$qryArrayList(text) {
	return getGsgJson("qryArrayList", text, arguments);
}

function $$qryTitleArrayList(text) {
	return getGsgJson("qryTitleArrayList", text, arguments);
}
   
function $$qryMap(text) {
	return getGsgJson("qryMap", text, arguments);
}

function $$qryList(text) {
	return getGsgJson("qryList", text, arguments);
}

function $$qryMapList(text) {
	return getGsgJson("qryMapList", text, arguments);
}

function $$qryEntity(text) {
	return getGsgJson("qryEntity", text, arguments);
}

function $$qryEntityList(text) {
	return getGsgJson("qryEntityList", text, arguments);
}

//Below methods return JSON's data 
function $gsg(text) {
	  if(text==null || text=="" || text.indexOf("FULL ")==0) 
		  return "";
	  return getGsgJsonData("", text, arguments);
}

function $java(text) {
	return getGsgJsonData("java", text, arguments);
}

function $javaTx(text) {
	return getGsgJsonData("javaTx", text, arguments);
}

function $qryObject(text) {
	return getGsgJsonData("qryObject", text, arguments);
}

function $qryArray(text) {
	return getGsgJsonData("qryArray", text, arguments);
}

function $qryArrayList(text) {
	return getGsgJsonData("qryArrayList", text, arguments);
}

function $qryTitleArrayList(text) {
	return getGsgJsonData("qryTitleArrayList", text, arguments);
}
 
function $qryMap(text) {
	return getGsgJsonData("qryMap", text, arguments);
}

function $qryList(text) {
	return getGsgJsonData("qryList", text, arguments);
}

function $qryMapList(text) {
	return getGsgJsonData("qryMapList", text, arguments);
}

function $qryEntity(text) {
	return getGsgJsonData("qryEntity", text, arguments);
}

function $qryEntityList(text) {
	return getGsgJsonData("qryEntityList", text, arguments);
}


function getGsgResponse(gsgMethod, text, args){
 var postJson;
 if (window.localStorage) {
   var develop_token=localStorage.getItem("develop_token");
   var token=localStorage.getItem("token");
   postJson= {"gsgMethod":gsgMethod, "develop_token":develop_token, "token":token, "$0": text};
 } else 
   postJson= {"gsgMethod":gsgMethod,"$0": text};
   for (var i = 1; i < args.length; i++) 
		  postJson["$"+i]=args[i]; 
  return $.ajax({
		type : 'POST',
		url : "/gsg.gsg",
		cache : false,
		dataType : "json",
		data : postJson,
		async : false
	}).responseText;
}

function gsgLogin(username, password){
	  var jsonStr= $.ajax({
			type : 'POST',
			url : "/gsg.gsg?login=true&username="+username+"&password="+password,
			cache : false,
			dataType : "json",
			data : {},
			async : false
		}).responseText;
	  return JSON.parse(jsonStr); 
}

function getGsgJson(gsgMethod, text, args){
	 var jsonOrHtml= getGsgResponse(gsgMethod, text, args); 
	 var jsonObj=JSON.parse(jsonOrHtml);
	  if(jsonObj.debugInfo!=null)
	      alert(jsonObj.debugInfo);  // or console.log(jsonObj.debugInfo);
	  return jsonObj;
}

function getGsgJsonData(gsgMethod, text, args){
	 var jsonOrHtml= getGsgResponse(gsgMethod, text, args); 
	 var jsonObj;  
	 try {
	   jsonObj=JSON.parse(jsonOrHtml);
	 } catch(e){
	 	return jsonOrHtml;
	 } 
	 if(jsonObj.debugInfo!=null)
	     alert(jsonObj.debugInfo);  // or console.log(jsonObj.debugInfo);
	 return jsonObj.data;
}


//serialize Object
$.fn.serializeObject = function(){
   var o = {};  
   var a = this.serializeArray();  
   $.each(a, function() {  
       if (o[this.name]) {  
           if (!o[this.name].push) {  
               o[this.name] = [o[this.name]];  
           }  
           o[this.name].push(this.value || '');  
       } else {  
           o[this.name] = this.value || '';  
       }  
   });  
   return o;  
};

//change a from to JSON string
function formToJSON(formName){ 
    var jsonuserinfo = $("#"+formName).serializeObject();
	return JSON.stringify(jsonuserinfo);
}  