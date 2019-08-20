function echoAdminTableView()
{
	// let info = fetchFromDb("getUsers", "table-body-A",true);
	var table = `
		<div id="main-body-admin" class="card1" style="width:60%; margin:auto">
			<div class="pad">
				<h1>Active Building: <span id="ActiveBuilding">loading...</span></h1>
				<h3>Change Building <select id="buildingDropDown"></select></h3>
			</div>
			<hr/>
			<div>
				<div id="inner-table-card" style="display: block;">
					<div class="table-heading" style="display: block;"><span class="table-name heading" style="text-align: left;">User Table</span>
						<span class="search-span" style="text-align: right;position:relative ;right: 50px;">
							<input type="text" id="search-input" class="searcher" placeholder="Search.." name="search"  onkeyup="search(this,'table-body-A')">
							<div class="search-button" id="admin-search-button" onclick="highlightSearchField()"><img class="search-icon" src="icons/search_grey.png"><i class="fa fa-search"></i>
							</div>
						</span>
					</div>
				<div>	
				<table style="width:100%" id="tbh" class="fixed_header">
					<thead>
						<tr>
							<td scope="col">Name</td>
							<td scope="col">Email</td>
							<td scope="col">Device_ID</td>
							<td scope="col">Type</td>
							<td scope="col">Status</td>
							<td scope="col">Edit</td>
						</tr>
					</thead>
					<tbody id="table-body-A">
					</tbody>
				</table>
			</div
		</div>

		<div style="text-align: right; padding-top: 1%; width: 100%;">
			<button id = "addButton" class="btn btn-light" onclick="displayOverlayWindow(windowForNewUser, null, null, null)">
				<img style=" width: 10px;" id="img-drop"  src="icons/baseline_add_black_48dp.png"> Add user 
			</button>
			<button id = "addButton"  class="btn btn-light" onclick="displayOverlayWindow(windowForNewBuilding, null, null, null)">
				<img style=" width: 10px;" id="img-drop" src="icons/baseline_add_black_48dp.png"> Add building 
			</button>
		</div>`;
	return table;	
}


function echoContentTable_SuperUser()
{
	let info = anotherFetch("getUsers", '#table-body-SU')
	var table =`
	<div>
		<div class="half" id="inner-table-card">				
			<div class="table-heading" style="display: block;">
				<span class="table-name heading" style="text-align: left;">User Table</span><span class="search-span" style="text-align: right; margin-left: 50%;">
					<input type="text" id="search-input" class="searcher" onkeyup="search(this,'table-body-SU')" placeholder="Search.." name="search">
					<div class="search-button" onclick="highlightSearchField()">
						<img class="search-icon" src="icons/search_grey.png">
						<i class="fa fa-search"></i>
					</div>
				</span>
			</div>
			<div >
  				<table class="fixed_header" style="fit-content" id="tbh">
				   <thead>
					    <tr>
							<th scope="col">Name</th>
							<th scope="col">Email</th>
							<th scope="col">Device_ID</th>
							<th scope="col">Type</th>
							<th scope="col">Status</th>
					    </tr>
					</thead>
				  	<tbody id="table-body-SU">
				  	</tbody>
				</table>
			</div>
		</div>
		<div class="half">
			<div class="pad">
				<button onclick="alarm(true,false)">Trigger Alarm</button>
				<button onclick="alarm(false,false)">Reset Alarm</button>
			</div>
			<hr/>
			<div class="pad">
				<h1>Active Building: <span id="ActiveBuilding">loading...</span></h1>
				<h3>Change Building <select id="buildingDropDown"></select></h3>
			</div>
			<div id="ActiveBuildingImage">
				<img id="buildingPicture" src="img/default.jpg">
			</div>
		</div>
	</div>
	`;
	return table;	
}
function echoTableBotview(){
	var table =`
	<div class="half">

			<div class="pad">
				<h1>All users</h1>
				<h1>Active Building: <span id="ActiveBuilding">loading...</span></h1>
			</div>
			<hr/>
		<h1 id="liveHeader">Simulation Building</h1>
		<table class="fixed_header" id="table-body-SU">
			<thead>
				<td>Name</td>
				<td>Email</td>
				<td>Device ID</td>
				<td>Type</td>
				<td>Status</td>
				<td class="special-long-td-text">Add to<br/> simulation</td>
			</thead>
			<tbody id="table-body-Sim"></tbody>
		</table>
	</div>
	<div class="half">
			<div class="pad">
				<button onclick="alarm(true,'simulation')">Trigger Alarm</button>
				<button onclick="alarm(false,'simulation')">Reset Alarm</button>
				<button onclick="" >Add Bot</button>
			</div>
			<div class="pad">
				<h1>Simulation</h1>
				<h1>Active Building: <span id="ActiveBuildingSim">loading...</span></h1>
				<h3>Change Building <select id="buildingDropDownSim"></select></h3>
			</div>
			<hr/>
		<table id="table-simulation">
			<thead>
				<td>Bot ID</td>
				<td>Location(Floor,x,z)</td>
				<td>Device ID</td>
				<td>Status</td>
			</thead>
			<tbody></tbody>
		</table>
	</div>
	`
	return table;
}