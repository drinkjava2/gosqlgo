function $gsg(text) {
	  if(text==null || text=="" || text.startsWith("FULL "))
		  return "";
	  return callRemote("", text, arguments);
}

function $java(text) {
	return callRemote("java", text, arguments);
}

function $javaTx(text) {
	return callRemote("javaTx", text, arguments);
}

function $qry(text) {
	return callRemote("qry", text, arguments);
}
  
function $qryArray(text) {
	return callRemote("qryArray", text, arguments);
}

function $qryArrayList(text) {
	return callRemote("qryArrayList", text, arguments);
}

function $qryTitleArrayList(text) {
	return callRemote("qryTitleArrayList", text, arguments);
}
   
function $qryMap(text) {
	return callRemote("qryMap", text, arguments);
}

function $qryMapList(text) {
	return callRemote("qryMapList", text, arguments);
}

function $qryEntityList(text) {
	return callRemote("qryEntityList", text, arguments);
}
 
function callRemote(gsgMethod, text, args){
 var postJson= {"gsgMethod":gsgMethod, "$0": text};
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
  
//serialize Object
$.fn.serializeObject = function()  
{  
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