var URL = "http://127.0.0.1:8080/"


function addDropDown(){
    $.ajax({
        url:  URL+"database",
        type: "POST",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: JSON.stringify({
            type: "getBuildings"
        }),
        success:function(res){
            if(res.status){
                // var mySelect = $('<select style="postion:relative" id="changeBuilding"></select>');
                $.each(res.msg, function(val,text) {
                    $("#building-change").append(
                        $('<option></option>').val(text).html(text)
                    );
                });
                // $("#building-change").append(mySelect);
            }
        }
    }); 
}
function changeBuilding(name){
    filePath = "/buildings/"+name +"/data.json";
    $.ajax({
            url : filePath,
            success : function (DATA) {
                if(DATA == null){
                    notify("Invalid Building", 'red');
                }
                $.ajax({
                    url: URL+"buildingGeneration",
                    type: "POST",
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    data : DATA,
                    success:function(res){
                        if(res.status){
                            notify("Building Changed", 'green');
                        }
                        else{

                        }
                        $global_building_info = getInfoAboutCurrentBuilding();
                        $("#building-name").text(name);
                        $("#SU-simulation").empty().text(getBuildingInfo("img"));
                    }   
                });
            },
            fail: function(data){
                console.log("failed: "+data);
            }
        });    
    
}