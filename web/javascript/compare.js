
function generateOutput(outputEl) {

	clearOuputContainer(outputEl);

	if(data == undefined) {

		alert('Compare data is not available! Please check that requried compareInfo.js file is present in javascript folder.');
		return ;
	}

	for(var p in data.compareData) {
		
		var divBtnPanel = document.createElement('DIV');
		divBtnPanel.height = "24px";

		var btnToggleCollapse = document.createElement('INPUT');
		btnToggleCollapse.id = "btnToggleCollapse_" + p;
		btnToggleCollapse.type = "button";
		btnToggleCollapse.value = "Toggle Collapse";
		divBtnPanel.appendChild(btnToggleCollapse);
		
		outputEl.appendChild(divBtnPanel);

		document.all["btnToggleCollapse_" + p].onclick = new Function("toggleCollapse(\"table_" + p + "\");");
		
		var btnGenerateScript = document.createElement('INPUT');
		btnGenerateScript.id = "btnGenerateScript_" + p;
		btnGenerateScript.type = "button";
		btnGenerateScript.value = "Generate Scripts";
		divBtnPanel.appendChild(btnGenerateScript);

		document.all["btnGenerateScript_" + p].onclick = new Function("generateDataSyncScript(\"" + p + "\", data.compareData, false);");
		
		var btnGenerateReverseScript = document.createElement('INPUT');
		btnGenerateReverseScript.id = "btnGenerateReverseScript_" + p;
		btnGenerateReverseScript.type = "button";
		btnGenerateReverseScript.value = "Generate Reverse Scripts";
		divBtnPanel.appendChild(btnGenerateReverseScript);

		document.all["btnGenerateReverseScript_" + p].onclick = new Function("generateDataSyncScript(\"" + p + "\", data.compareData, true);");
		
		var txtEntityName = document.createElement('INPUT');
		txtEntityName.type = "text";
		txtEntityName.value = p;
		txtEntityName.style.border = "0px";
		txtEntityName.style.margin = "0px";
		txtEntityName.style.width = "200px";
		divBtnPanel.appendChild(txtEntityName);

		var tableEl = generateDiffDetail(p, data.compareData);
		tableEl.id = "table_" + p;
		
		var containerEl = createDiffDetailContainer(p, data.compareData);
		containerEl.appendChild(tableEl);
		outputEl.appendChild(containerEl);
	}
}

function createDiffDetailContainer(p, compareData) {

	var divDifDetailContainer = document.createElement('DIV');
	divDifDetailContainer.id = "diffDetailContainer_" + p;
	divDifDetailContainer.style.width = "auto";
	divDifDetailContainer.style.height = "auto";
	divDifDetailContainer.style.overflow = "auto";
	divDifDetailContainer.style.border = "1px solid red";

	return divDifDetailContainer;
}

function toggleCollapse(id) {

	if(document.all[id].style.display == "none") {
		
		document.all[id].style.display = "block";
	} else {
		
		document.all[id].style.display = "none";
	}
}

function generateAllDataSyncScript() {
	
	var sbScripts = new StringBuffer();
	var compareData = data.compareData;
	
	for(var p in compareData) {
		
		var entityName = p;
		var entityData = compareData[entityName];

		sbScripts.append('-- Data sync script for entity: ' + entityName + '\n');
		sbScripts.append('\n');
		sbScripts.append('-- UPDATE SCRIPTS\n');
		// 1st generate script for different data
		var updateScripts = generateDataSyncUpdateScript(entityName, entityData);
		if(updateScripts != undefined) {
			sbScripts.append(updateScripts);
		}
		
		sbScripts.append('\n');
		sbScripts.append('-- DELETE scripts for deleted data\n');
		// 2nd generate script for missing data
		var deleteScripts = generateDataSyncDeleteScript(entityName, entityData);
		if(deleteScripts != undefined) {
			sbScripts.append(deleteScripts);
		}
		
		sbScripts.append('\n');
		sbScripts.append('-- INSERT scripts for new data\n');
		// 3rd generate script for new data
		var insertScripts = generateDataSyncInsertScript(entityName, entityData);
		if(insertScripts != undefined) {
			sbScripts.append(insertScripts);
		}
	}
	

	showContent(sbScripts.toString());
	sbScripts.flush();
}

function generateDataSyncScript(entityName, compareData, reverse) {

	var entityData = compareData[entityName];

	var sbScripts = new StringBuffer();
	sbScripts.append('-- Data sync script for entity: ' + entityName + '\n');
	sbScripts.append('\n');
	sbScripts.append('-- UPDATE SCRIPTS\n');
	// 1st generate script for different data
	var updateScripts = generateDataSyncUpdateScript(entityName, entityData, reverse);
	if(updateScripts != undefined) {
		sbScripts.append(updateScripts);
	}
	
	sbScripts.append('\n');
	sbScripts.append('-- DELETE scripts for deleted data\n');
	// 2nd generate script for missing data
	var deleteScripts = generateDataSyncDeleteScript(entityName, entityData, reverse);
	if(deleteScripts != undefined) {
		sbScripts.append(deleteScripts);
	}
	
	sbScripts.append('\n');
	sbScripts.append('-- INSERT scripts for new data\n');
	// 3rd generate script for new data
	var insertScripts = generateDataSyncInsertScript(entityName, entityData, reverse);
	if(insertScripts != undefined) {
		sbScripts.append(insertScripts);
	}

	showContent(sbScripts.toString());
	sbScripts.flush();
}

function generateDataSyncUpdateScript(entityName, entityData, reverse) {

	if(entityData.diffData == undefined) {

		return "";
	}
	
	var sb = new StringBuffer();
	
	for(var i = 0; i < entityData.diffData.length; ++i) {

		sb.append('UPDATE ');
		sb.append(entityName);
		sb.append(' SET ');

		var keyValue = [];
		var count = 0;
		
		var valueIndex = reverse == true ? 1 : 0;

		// first find columns in difference
		for(var j = 0; j < entityData.diffData[i][0].length; ++j) {

			for(var k = 0; k < entityData.key.length; ++k) {
				
				if(entityData.schemas[j].fieldName == entityData.key[k]) {
					
					keyValue[k] = getSQLValue(entityData.schemas[j].type, entityData.diffData[i][0][j]);
				}
			}
			
			if(entityData.diffData[i][0][j] != entityData.diffData[i][1][j]) {

				if(count > 0) {
					
					sb.append(', ');
				} else {
				}
				sb.append(entityData.schemas[j].fieldName);
				sb.append(' = ');
				sb.append(getSQLValue(entityData.schemas[j].type, entityData.diffData[i][valueIndex][j]));
				sb.append(' ');
				count++;
			} 
		}
		
		sb.append('WHERE ');
		for(var k = 0; k < entityData.key.length; ++k) {
			
			if(k > 0) {
				
				sb.append(' AND ');
			}
			sb.append(entityData.key[k]);
			sb.append(' = ');
			sb.append(keyValue[k]);
		}
		
		sb.append(';\n');
	}

	return sb.toString();
}

function getSQLValue(type, value) {

	if(value == undefined) {
		return 'NULL';
	}
	if(type != BIT 
			&& type != TINYINT 
			&& type != SMALLINT
			&& type != INTEGER
			&& type != BIGINT
			&& type != FLOAT
			&& type != REAL
			&& type != DOUBLE
			&& type != NUMERIC
			&& type != DECIMAL
			&& type != BOOLEAN) {
		value = value.replace("'", "''");
		value = '\'' + value + '\'';
	}
	if (type == DATE) {
		value = "to_date(" + value + ", 'YYYY-MM-DD')";
	}
	if (type == TIMESTAMP) {
		value = "to_date(" + value + ", 'YYYY-MM-DD HH24:MI:SS')";
	}
	return value;
}

function generateDataSyncDeleteScript(entityName, entityData, reverse) {

	var data = entityData.missingData;
	
	if(reverse == true) {
		
		data = entityData.newData;
	}
	
	if(data == undefined) {

		return "";
	}

	var sb = new StringBuffer();
	
	for(var i = 0; i < data.length; ++i) {

		sb.append('DELETE FROM ');
		sb.append(entityName);

		var keyValue = [];

		// first find columns in difference
		for(var j = 0; j < data[i].length; ++j) {

			for(var k = 0; k < entityData.key.length; ++k) {
				
				if(entityData.schemas[j].fieldName == entityData.key[k]) {
					
					keyValue[k] = getSQLValue(entityData.schemas[j].type, data[i][j]);
				}
			}
		}
		
		sb.append(' WHERE ');
		for(var k = 0; k < entityData.key.length; ++k) {
			
			if(k > 0) {
				
				sb.append(' AND ');
			}
			sb.append(entityData.key[k]);
			sb.append(' = ');
			sb.append(keyValue[k]);
		}
		
		sb.append(';\n');
	}

	return sb.toString();
}

function generateDataSyncInsertScript(entityName, entityData, reverse) {

	var data = entityData.newData;
	
	if(reverse == true) {
		
		data = entityData.missingData;
	}
	
	if(data == undefined) {

		return "";
	}

	var sb = new StringBuffer();

	var sbFieldNames = new StringBuffer();
	
	sbFieldNames.append('(');
	
	for(var i = 0; i < entityData.schemas.length; ++i) {

		if(i > 0) {
			
			sbFieldNames.append(',');
		}
		sbFieldNames.append(entityData.schemas[i].fieldName)
	}
	sbFieldNames.append(')');
	
	for(var i = 0; i < data.length; ++i) {

		sb.append('INSERT INTO ');
		sb.append(entityName);
		sb.append(' ');
		sb.append(sbFieldNames.toString());
		sb.append(' VALUES(');

		var keyValue;

		// first find columns in difference
		for(var j = 0; j < data[i].length; ++j) {

			if(j > 0) {
				
				sb.append(', ');
			}
			sb.append(getSQLValue(entityData.schemas[j].type, data[i][j]));
		}
		
		sb.append(');\n');
	}

	return sb.toString();
}

function generateDiffDetail(entityName, compareData) {

	var entityData = compareData[entityName];
	
	var tableEl = document.createElement('TABLE');
	
	var tbody = document.createElement('TBODY');
	
	tableEl.appendChild(tbody);

	createHeader(entityName, tbody, entityData);

	createBody(entityName, tbody, entityData);
	
	return tableEl;
}

function createHeader(entityName, tbody, entityData) {
	// build header
	var hdrow = document.createElement('TR');
	
	var hdcell = addNewCell(hdrow);
	
	hdcell.className = "headerCell";

	hdcell.colSpan = entityData.schemas.length;
	
	hdcell.innerText = entityName;

	hdcell.style.backgroundColor = "silver";
	 
	tbody.appendChild(hdrow);
	
	hdrow = document.createElement('TR');

	for(var i = 0; i < entityData.schemas.length; ++i) {
		
		hdcell = addNewCell(hdrow);
		
		hdcell.className = "headerCell";
		 
		hdcell.innerText = entityData.schemas[i].fieldName;
	}
	
	tbody.appendChild(hdrow);
}

function createBody(entityName, tbody, entityData) {

	createDiffData(tbody, entityData);

	createMissingData(tbody, entityData);

	createNewData(tbody, entityData);
	
}

function createDiffData(tbody, entityData) {

	if(entityData.diffData == undefined) {

		return ;
	}
	
	var hdrow = document.createElement('TR');

	var hdcell = addNewCell(hdrow);
	
	hdcell.className = "headerCell";

	hdcell.colSpan = entityData.schemas.length;
	
	var excludeIndex = [];
	for (var i = 0; i < entityData.exclude.length; i++) {
		for (var j = 0; j < entityData.schemas.length; j++) {
			if (entityData.schemas[j].fieldName == entityData.exclude[i]) {
				excludeIndex.push(j);
			}
		}
	}
	
	
	hdcell.innerText = "Different data";

	hdcell.style.backgroundColor = "lightgreen";
	 
	tbody.appendChild(hdrow);
	
	for(var i = 0; i < entityData.diffData.length; ++i) {

		var bdrow1 = document.createElement('TR');
		var bdrow2 = document.createElement('TR');

		for(var j = 0; j < entityData.diffData[i][0].length; ++j) {
			
			var bdcell1 = addNewCell(bdrow1);
			var bdcell2 = addNewCell(bdrow2);
			
			bdcell1.className = "dataCell";
			bdcell2.className = "dataCell";
		
			bdcell1.style.backgroundColor = ((i % 2) == 0) ? "cyan" : "white";
			bdcell2.style.backgroundColor = ((i % 2) == 0) ? "cyan" : "white";

			bdcell1.innerText = entityData.diffData[i][0][j] != undefined ? entityData.diffData[i][0][j] : ' ';
			bdcell2.innerText = entityData.diffData[i][1][j] != undefined ? entityData.diffData[i][1][j] : ' ';

			if (excludeIndex.includes(j)) {
				continue ;
			}
			
			if (entityData.diffData[i][0][j] != entityData.diffData[i][1][j]) {

				bdcell1.className += " cBlue";	
				bdcell2.className += " cGreen";	
			}
		}
		
		tbody.appendChild(bdrow1);
		tbody.appendChild(bdrow2);
	}
	
}

function createMissingData(tbody, entityData) {

	if(entityData.missingData == undefined) {

		return ;
	}
	
	var hdrow = document.createElement('TR');

	var hdcell = addNewCell(hdrow);
	
	hdcell.className = "headerCell";

	hdcell.colSpan = entityData.schemas.length;
	
	hdcell.innerText = "Missing Data";

	hdcell.style.backgroundColor = "lightgreen";
	 
	tbody.appendChild(hdrow);
	
	for(var i = 0; i < entityData.missingData.length; ++i) {

		var bdrow = document.createElement('TR');

		for(var j = 0; j < entityData.missingData[i].length; ++j) {
			
			var bdcell = addNewCell(bdrow);
			
			bdcell.className = "dataCell";
		
			bdcell.style.backgroundColor = ((i % 2) == 0) ? "cyan" : "white";

			bdcell.innerText = entityData.missingData[i][j] != undefined ? entityData.missingData[i][j] : ' ';

		}
		
		tbody.appendChild(bdrow);
	}
}

function createNewData(tbody, entityData) {

	if(entityData.newData == undefined) {

		return ;
	}
	
	var hdrow = document.createElement('TR');

	var hdcell = addNewCell(hdrow);
	
	hdcell.className = "headerCell";

	hdcell.colSpan = entityData.schemas.length;
	
	hdcell.innerText = "New Data";

	hdcell.style.backgroundColor = "lightgreen";
	 
	tbody.appendChild(hdrow);
	
	for(var i = 0; i < entityData.newData.length; ++i) {

		var bdrow = document.createElement('TR');

		for(var j = 0; j < entityData.newData[i].length; ++j) {
			
			var bdcell = addNewCell(bdrow);
			
			bdcell.className = "dataCell";
		
			bdcell.style.backgroundColor = ((i % 2) == 0) ? "cyan" : "white";

			bdcell.innerText = entityData.newData[i][j] != undefined ? entityData.newData[i][j] : ' ';

		}
		
		tbody.appendChild(bdrow);
	}

}

function addNewCell(trEl) {

	var tdEl = document.createElement('TD');
	
	trEl.appendChild(tdEl);
	
	return tdEl;
}

function clearOuputContainer(outputEl) {
	outputEl.innerHTML = "";
}

function hide(divEl) {

	divEl.style.display = "none";
}
