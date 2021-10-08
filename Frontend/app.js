const SERVER_URL = "http://localhost:8080/H2HBABBA3242"
// const SERVER_URL = "http://localhost:5500/db.json"

// global variable used for storing ID of each checkbox, is used for enabling and disabling EDIT and DELETE buttons.
let idArray = []
let FETCH_API_RESULTS_ARR = []
let totalRowCount;
let addBtnModal = document.getElementById("addBtnModal")
let editBtnModal = document.getElementById("editBtnModal")
let deleteBtnModal = document.getElementById("deleteBtnModal")
const checkAllBoxes = document.getElementById("checkAll")
const checkBoxes = document.getElementsByClassName("checkbox")
let table = document.getElementById("table"); 
let thead = document.getElementById("thead"); 
let emptySearchDiv = document.getElementById("noData")


/*----------------------API calls-----------------------------*/

const fetchData = async (url, data)=>{
    return await fetch(url, {
        method: "POST",
        body: JSON.stringify(data)
    }).then((response)=>{
      // console.log(`response in fetchData`, response)
      return response.json()
    }).catch((err)=>{
      console.error(`Error in fetchData: `, err)
    })
}

const sendData = async (url,data)=>{
  return await fetch(url, {
      method: "POST",
      body: JSON.stringify(data)
  }).then((response)=>{
    // console.log(`response in fetchData`, response)
    return response.json()
  }).catch((err)=>{
    console.error(`Error in fetchData: `, err)
  })
}

const updateData = async (url,data)=>{
  return await fetch(url, {
      method: "PUT",
      body: JSON.stringify(data)
  }).then((response)=>{
    // console.log(`response in fetchData`, response)
    return response.json()
  }).catch((err)=>{
    console.error(`Error in fetchData: `, err)
  })
}

const deleteData = async (url,data)=>{
  return await fetch(url, {
      method: "DELETE",
      body: JSON.stringify(data)
  }).then((response)=>{
    // console.log(`response in fetchData`, response)
    return response.json()
  }).catch((err)=>{
    console.error(`Error in fetchData: `, err)
  })
}

/*----------------------API calls-----------------------------*/


/*-----------------------------PAGINATION------------------------------*/

// global counter to determine ehich set of 11 to print
let count = 0
let rowsPerPage = 11

// function to disable/enable arrow buttons
function toggleArrowState(){
  let lastPageNo = Math.floor(totalRowCount/rowsPerPage)
  if(count==0){
    disableArrow("left-arrow")
    enableArrow("right-arrow")
  }else if(count==lastPageNo){
    disableArrow("right-arrow")
    enableArrow("left-arrow")
  }else if(count>0 && count<lastPageNo-1){
    enableArrow("right-arrow")
    enableArrow("left-arrow")
  }
}

// Disable Arrow Function
function disableArrow(id){
  let arrow = document.getElementById(id);
  arrow.children[0].setAttribute("class","unSelectedBtn")
  arrow.children[0].disabled = true
  arrow.children[0].style.cursor="not-allowed";
}

// Enable Arrow Function
function enableArrow(id){
  let arrow = document.getElementById(id);
  arrow.children[0].setAttribute("class","selectedBtn")
  arrow.children[0].disabled = false
  arrow.children[0].style.cursor="pointer";
}

// Function called when Next Arrow is clicked
async function loadMore(){
  // console.log(`count(loadMore): `, count)
  if((FETCH_API_RESULTS_ARR.length%rowsPerPage==0) && (count<Math.floor(totalRowCount/rowsPerPage)-1)){
    count++;
    toggleArrowState()
    await loadData()
  }else{
    console.log("Count cannot be more than length of Array")
  }
}

// Next Arrow OnClick
let nextArrow = document.getElementById("right-arrow")
nextArrow.children[0].onclick = async (res)=>{
  await loadMore()
}

// Function called when Previous Arrow is clicked
async function loadPrevious(){
  // console.log(`count(loadPrevious): `, count)
  if(count>0){
    count--;
    toggleArrowState()
    await loadData()
  }else{
    console.log("Count cannot be less than 0")
  }
}

// Previous Arrow OnClick
let prevArrow = document.getElementById("left-arrow")
prevArrow.children[0].onclick = async (res)=>{
  await loadPrevious()
}


// Function to fetch 11 results from global Array
async function loadData(){
  try{
    // console.log(`count in loadData: `, count)
    // make global array length = 0 so that any previous data does not interfere with incoming data
    FETCH_API_RESULTS_ARR.length=0;
 
    // call fetch api with page number and store api response in global array

    // FETCH_API_RESULTS_ARR = await fetchData(`${SERVER_URL}/Read?page=${count}`)
    FETCH_API_RESULTS_ARR = await fetchData(`${SERVER_URL}/Read`,count)
    // console.log(`FETCH_API_RESULTS_ARR Results in loadData(): `, FETCH_API_RESULTS_ARR)

    // pass to display function
    displayData(FETCH_API_RESULTS_ARR)
  }catch(err){
    console.error("Server Error(loadData): ", err);
  }
}

/*--------------------------------MAIN WINDOW EVENT LISTENER--------------------------- */

// Fetching Data on Load (currently from JSON File will change to DB later on)
window.addEventListener("DOMContentLoaded",async ()=>{
  await onLoad()
})

async function onLoad(){
  try{
    // Setting count to 0 so when page is reloaded the first 11 data showup
    count=0
    // const response = await fetchData(`${SERVER_URL}/Read?page=${count}`)
    const response = await fetchData(`${SERVER_URL}/Read`,count)
    // console.log(`Response of fetchApi in DOMContentLoaded: `, response)

    // store the id on the last row in a global variable;
    totalRowCount = response[0].id
    console.log(`totalRowCount: `, totalRowCount)

    disableArrow("left-arrow")
    if(response.length==0){
      table.style.display = "none"
      thead.style.display = "none"
    }else{
      table.style.display = "block"
      thead.style.display = "block"
      await loadData()
    }
    // Seting ID Array to empty so that when page is reloaded it has no values
    idArray=[] 
  }catch(err){
    console.error(`Server Error(fetch): `, err)
  }
}

/* -----------------SEARCH FUNCTIONALITY ---------------*/
let originalData=[], filteredData = [];
let searchBar = document.getElementById("searchBar")
let clearSearchBtn = document.getElementById("clearSearchBtn")
searchBar.addEventListener("keyup",(event)=>{
  let searchQuery = event.target.value;
  originalData = FETCH_API_RESULTS_ARR
  // console.log("Original Data: ",originalData)
  // console.log(`searchQuery: `, searchQuery)
  if(searchQuery!=""){
    filteredData = originalData.filter(data=>{
      if(data.invoiceID.includes(searchQuery)){
        return data;
      }
    })
  }else{
    displayData(FETCH_API_RESULTS_ARR)
  }
  if(filteredData.length==0){
    table.style.display="none"
    thead.style.display="none"
    emptySearchDiv.style.display="block"
    nextArrow.style.display="none"
    prevArrow.style.display="none"
  }else{
    table.style.display="block"
    thead.style.display="block"
    emptySearchDiv.style.display="none"
    nextArrow.style.display="block"
    prevArrow.style.display="block"
    displayData(filteredData)
  }
})

clearSearchBtn.onclick = (event)=>{
  searchBar.value=""
}

// Disable button feature
function disableBtn(btnId) {
  let btn = document.getElementById(btnId);
  btn.style.cursor = "not-allowed";
  btn.setAttribute("class","btn disabledBtn")
  btn.disabled = true;
}

// Enable button feature
function enableBtn(btnId) {
  let btn = document.getElementById(btnId);
  btn.setAttribute("class","btn enabledBtn")
  btn.style.cursor = "pointer";
  btn.disabled = false;
}

// Disable Add Button in Add Modal
function disableAddBtn() {
  let btn = addBtnModal;
  btn.style.cursor = "not-allowed";
  btn.disabled = true;
  btn.style.backgroundColor = "#97A1A9";
}

// Enable Add Button in Add Modal
function enableAddBtn() {
  let btn = addBtnModal;
  btn.style.cursor = "pointer";
  btn.disabled = false;
  btn.style.backgroundColor = "#14AFF1";
}

// Disable Save Button in Edit Modal
function disableSaveBtn() {
  let btn = editBtnModal;
  btn.style.cursor = "not-allowed";
  btn.disabled = true;
  btn.style.backgroundColor = "#97A1A9";
}

// Enable Save Button in Edit Modal
function enableSaveBtn() {
  let btn = editBtnModal;
  btn.style.cursor = "pointer";
  btn.disabled = false;
  btn.style.backgroundColor = "#14AFF1";
}

// Disable Delete Button in Delete Modal
function disableDeleteBtn() {
  let btn = deleteBtnModal;
  btn.style.cursor = "not-allowed";
  btn.disabled = true;
  btn.style.backgroundColor = "#97A1A9";
}

// Enable Delete Button in Delete Modal
function enableDeleteBtn() {
  let btn = deleteBtnModal;
  btn.style.cursor = "pointer";
  btn.disabled = false;
  btn.style.backgroundColor = "#14AFF1";
}

// Disable edit and delete buttons initially
disableBtn("editBtn")
disableBtn("deleteBtn")

/*---------------------Selecting All Checkboxes---------------------- */

checkAllBoxes.onclick = (ele)=>{
  if(ele.target.checked){
    disableBtn("addBtn")
    enableBtn("deleteBtn")
    disableArrow("left-arrow")
    disableArrow("right-arrow")
    // Code to select all checkboxes
    /* NOTE: Since checkboxes returns a HTML Collection and not an array,
     we need to extract values as an array using Array.from() */
    Array.from(checkBoxes).forEach(checkbox=>{
      checkbox.checked = true
      idArray.push(checkbox.value)
    })
    // console.log(`idArray in select all checkboxes: `, idArray)
  }else{
    enableBtn("addBtn")
    disableBtn("deleteBtn") 
    // Code to deselect all checkboxes
    toggleArrowState()
    Array.from(checkBoxes).forEach(checkbox=>{
      checkbox.checked = false
      idArray.pop(checkbox.value)
    })
    // console.log(`idArray in de-select all checkboxes: `, idArray)
  }
}

/*---------------------Selecting Single Checkboxes---------------------- */

// Code to select single checkbox
function singleCheckbox(id,checked){
  // console.log(`Inside singelCheckbox function`, id,checked)
  
  if(checked){
    idArray.push(id)
    disableArrow("left-arrow")
    disableArrow("right-arrow")
  }
  else {
    idArray = idArray.filter((elementId)=> elementId!=id)
    // handling when header checkbox is checked and an individual checkbox is unchecked then the arrow buttons should be disabled and the header checkbox should be unchecked
    if(checkAllBoxes.checked){
      disableArrow("left-arrow")
      disableArrow("right-arrow")
      checkAllBoxes.checked=false;  
    }else{
    // handling for unchecking single checkbox, also verifying if all chckboxes are unchecked or not.
      if(idArray.length>0){
        disableArrow("left-arrow")
        disableArrow("right-arrow")  
      }else{
        toggleArrowState()
      }
    }
  }
  // console.log(`idArray inside singleCheckBox`, idArray)
  checkCount(idArray)
}

// Function to count number of checkboxes clicked and enable/disable buttons accordingly
function checkCount(arr){
  // console.log(`Array values are: `, arr)
  if(arr.length==1){
    enableBtn("editBtn")
    enableBtn("deleteBtn") 
    disableBtn("addBtn") 
  }else if(arr.length==0){
    disableBtn("deleteBtn") 
    disableBtn("editBtn")
    enableBtn("addBtn") 
  }else{    
    enableBtn("deleteBtn")
    disableBtn("editBtn") 
    disableBtn("addBtn") 
  }
}

function getDataByID(id){
  // console.log("id in getDataByID: ",id)
  // id=parseInt(id[0])
  let data = FETCH_API_RESULTS_ARR.filter((res)=>{
    // console.log("Response in getDataByID is: ",res.id,id,res.id==id)
    if(res.id==id)
      return res;
  })
  // console.log(`Data in getDataById is: `, data)
  return data
}

// Get the modal
let addModal = document.querySelector("#addModal");
let editModal = document.querySelector("#editModal");
let deleteModal = document.querySelector("#deleteModal");

// Get the button that opens the modal
let addBtn = document.querySelector("#addBtn");
let editBtn = document.querySelector("#editBtn");
let deleteBtn = document.querySelector("#deleteBtn");

// Get the <span> element that closes the modal
let span = document.querySelectorAll(".close");
let addSpan = document.querySelectorAll(".close")[0];
let editSpan = document.querySelectorAll(".close")[1];
let deleteSpan = document.querySelectorAll(".close")[2];

// Fields in Edit Modal
let editAmount = document.getElementById("editInvoiceAmount")
let editNotes = document.getElementById("editNotes")

// When the user clicks the button, open the modal
addBtn.onclick = function () {
  addModal.style.display = "block";
};
editBtn.onclick = function () {
  editModal.style.display = "block";
  // If EDIT button is enabled it means idArray has only 1 value. Implying that idArray can directly be passed as parameter.
  const data = getDataByID(idArray);
  // console.log(`data in editBtn onclick:`, data[0])
  editAmount.setAttribute("placeholder",data[0].totalAmount)
  let notesPlaceholder = data[0].notes
  notesPlaceholder = notesPlaceholder.substring(0, 20) //cuts to 20
  last = notesPlaceholder.lastIndexOf(" ") //gets last space (to avoid cutting the middle of a word)
  notesPlaceholder = notesPlaceholder.substring(0, (last>0)?last:20) //cuts from last space (to avoid cutting the middle of a word)
  notesPlaceholder = notesPlaceholder + `...` //adding ... to the end of the text
  editNotes.setAttribute("placeholder",notesPlaceholder)
};
deleteBtn.onclick = function () {
  deleteModal.style.display = "block";
};

// When the user clicks on <span> (x), close the modal
addSpan.onclick = function () {
  addModal.style.display = "none";
  clearInputAddModal();
};
editSpan.onclick = function () {
  editModal.style.display = "none";
  clearInputEditModal()
};
deleteSpan.onclick = function () {
  deleteModal.style.display = "none";
};

// When the user clicks anywhere outside of the modal, close it
window.onclick = function (event) {
  if (event.target == addModal) {
    addModal.style.display = "none";
    clearInputAddModal()
  }
  if (event.target == editModal) {
    editModal.style.display = "none";
    clearInputEditModal()
  }
  if (event.target == deleteModal) {
    deleteModal.style.display = "none";
  }
};

// NOTE: Common for All Modals,
// When user clicks cancel modal should close.
const cancelButtons = document.querySelectorAll(".cancelBtn");
const cancelBtnAddModal = cancelButtons[0];
cancelBtnAddModal.onclick = function () {
  addModal.style.display = "none";
};
const cancelBtnEditModal = cancelButtons[1];
cancelBtnEditModal.onclick = function () {
  editModal.style.display = "none";
};
const cancelBtnDeleteModal = cancelButtons[2];
cancelBtnDeleteModal.onclick = function () {
  deleteModal.style.display = "none";
};

/* --------------Add Button Functionality---------------*/
// Clear Input Function
function clearInputValue(ids) {
  ids.forEach((id) => {
    let element = document.getElementById(id);
    element.value = null;
  });
  disableAddBtn();
}

// Clear Input Function call for Add Modal
function clearInputAddModal() {
  clearInputValue([
    "customerName",
    "customerNumber",
    "invoiceNumber",
    "invoiceAmount",
    "dueDate",
    "notes",
  ]);
}

// Clear Input Function call for Edit Moda;
function clearInputEditModal(){
  clearInputValue(["editInvoiceAmount","editNotes"])
}

// Initially Disbale Add Button in Add Modal
disableAddBtn();

// Enabling Add Button in Add Modal based on required input field
let cName = document.getElementById("customerName");
let cNumber = document.getElementById("customerNumber");
let invoiceNo = document.getElementById("invoiceNumber");
let amount = document.getElementById("invoiceAmount");
let dueDate = document.getElementById("dueDate");
let notes = document.getElementById("notes");
let c1 = 0,c2 = 0,c3 = 0,c4 = 0,c5 = 0;
cName.addEventListener("keyup", (ele) => {
  c1 = ele.target.value != "" ? 1:0;
  (c1 && c2 && c3 && c4 && c5)?enableAddBtn():disableAddBtn();
});
cNumber.addEventListener("keyup", (ele) => {
  c2 = ele.target.value != "" ? 1:0;
  (c1 && c2 && c3 && c4 && c5)?enableAddBtn():disableAddBtn();
});
invoiceNo.addEventListener("keyup", (ele) => {
  c3 = ele.target.value != "" ? 1:0;
  (c1 && c2 && c3 && c4 && c5)?enableAddBtn():disableAddBtn();
});
amount.addEventListener("keyup", (ele) => {
  c4 = ele.target.value != "" ? 1:0;
  (c1 && c2 && c3 && c4 && c5)?enableAddBtn():disableAddBtn();
});
dueDate.addEventListener("keyup", (ele) => {
  c5 = ele.target.value != "" ? 1:0;
  (c1 && c2 && c3 && c4 && c5)?enableAddBtn():disableAddBtn();
});

// Add API
async function addAPI(){
  try{
    let data = {
      customerName:cName.value,
      customerNo: cNumber.value,
      invoiceID: invoiceNo.value,
      totalAmount: amount.value,
      dueDate: dueDate.value,
      notes: notes.value
    }
    // console.log("Data in addAPI is: ",data)
    disableAddBtn()
    // let queryParams = `customerName=${data.customerName}&customerNo=${data.customerNumber}&invoiceNo=${data.invoiceNumber}&amount=${data.amount}&dueDate=${data.dueDate}&notes=${data.notes}`;
    const response = await sendData(`${SERVER_URL}/Insert`,data);
    enableAddBtn()
    toggleArrowState()
    addModal.style.display="none";
    await onLoad()
    // if(response.status==200){

    // }
    console.log(`response of add API is: `, response)
  }catch(err){
    console.error("Server Error(add): ",err);
  }
}

// Onclick Add Button in Add Modal addAPI is called
async function callAddAPI(){
  await addAPI()
}

/*--------------------Edit Button Functionality---------------------------*/

// Reset Input to original Values for Edit Modal
function resetData(){
  editAmount.value=null
  editNotes.value=null
}

// Edit API
async function editAPI(){
  try{
    const fetchData = getDataByID(idArray)
    // console.log(`fetchData in editAPI`, fetchData[0].notes, fetchData[0].totalAmount)
    // console.log("isTrue: ",editNotes.value==undefined,editNotes=="",editNotes===undefined,editNotes==="")
    let data = {
      id: fetchData[0].id,
      totalAmount: editAmount.value,
      notes: editNotes.value
    }
    console.log(`editAmount: ${editAmount.value}`,`editNotes: ${editNotes.value}`)
    disableSaveBtn()
    if(editAmount.value==undefined){
      data.totalAmount = fetchData[0].totalAmount
    }else if(editNotes.value==undefined){
      data.notes = fetchData[0].notes
    }
    // let queryParams = `id=${data.id}&amount=${data.amount}&notes=${data.notes}`
    // const response = await updateData(`${SERVER_URL}/Update?${queryParams}`,data)
    const response = await updateData(`${SERVER_URL}/Update`,data)
    console.log(`response of edit API is: `, response)
    enableSaveBtn()
    resetData()
    toggleArrowState()
    editModal.style.display="none";
    await onLoad()
    enableBtn("addBtn")
    disableBtn("editBtn")
    disableBtn("deleteBtn")
  }catch(err){
    console.error("Server Error(edit): ",err);
  }
}
// Onclick Save Button in Edit Modal editAPI is called
async function callEditAPI(){
  await editAPI()
}

/*--------------------Delete Button Functionality---------------------------*/

// Delete API

async function deleteAPI(){
  try{
    // data is an Array containing all the clicked checkboxes ID, the ID are in integer format
    let data = idArray;
    disableDeleteBtn()
    // console.log("Data in deleteAPI is: ",data)
    let convertedData="";
    data.forEach(id=>{
      let x = parseInt(id)
      convertedData+=`${x},`
    })
    // console.log(`convertedData`, convertedData)
    // let queryParams=`id=${convertedData}`
    // const response = await deleteData(`${SERVER_URL}/Delete?${queryParams}`,data)
    const response = await deleteData(`${SERVER_URL}/Delete`,convertedData)
    console.log(`response of delete API is: `, response)
    enableDeleteBtn()
    deleteModal.style.display="none";
    toggleArrowState()
    await onLoad()
    enableBtn("addBtn")
    disableBtn("editBtn")
    disableBtn("deleteBtn")
  }catch(err){
    console.error(`Server Error(delete): `,err)
  }
}

// Onclick Delete Button in Delete Modal deleteAPI is called
async function callDeleteAPI(){
  await deleteAPI()
}

// Dynamically creating tbody
let tbody = document.createElement("tbody");
tbody.setAttribute("id","tbody")
table.appendChild(tbody);

// Display data in table
function displayData(dataArr){
  try{
    // console.log(`dataArr in displayData`, dataArr)
    tbody.innerHTML=""
    dataArr.forEach((data)=>{
      // console.log(`data in displayData`, data)
      if(data!==undefined){
        let {customerName,customerNo,dueDate,id,totalAmount,invoiceID,notes,predictedPaymentDate} = data
        // handling null predicted payment dates
        if(predictedPaymentDate===undefined)
          predictedPaymentDate="--"
  
        // Creating row
        let row = document.createElement("tr");

        // 8 UI columns
        let id_cell = createInput(id);
        let cName_cell = createCell(customerName);
        let cNumber_cell = createCell(customerNo);
        let invoiceNo_cell = createCell(invoiceID);
        let amount_cell = createCell(totalAmount);
        let dueDate_cell = createCell(dueDate);
        let predictedDate_cell = createCell(predictedPaymentDate);
        let notes_cell = createNoteCell(notes); 

         // handling duedate whose date has passed
         if(Date.parse(dueDate)<Date.now()){
          dueDate_cell.children[0].style.color="#FF5B5B"
        } 
        
        // Append Columns to Row
        row.append(id_cell,cName_cell,cNumber_cell,invoiceNo_cell,amount_cell,dueDate_cell,predictedDate_cell,notes_cell);
        // Append Row to Table
        tbody.appendChild(row);
      }else{
        console.log(`Data undefined in displayData()` )
      }
    })
  }catch(err){
    console.error(`Error in displayData function`,err)
  }
}

// Create Cell Function
function createCell(data){
  let td = document.createElement("td")
  let pTag = document.createElement("p")
  pTag.textContent = data
  td.appendChild(pTag)
  return td
}

// Create Notes Cell
function createNoteCell(data){
  let td = document.createElement("td")
  let pTag = document.createElement("p")
  if(data.length >= 30) {
    data = data.substring(0, 30) //cuts to 30
    last = data.lastIndexOf(" ") //gets last space (to avoid cutting the middle of a word)
    data = data.substring(0, last) //cuts from last space (to avoid cutting the middle of a word)
    data = data + `...` //adds (...) at the end to show that it's cut
  }
  pTag.textContent = data
  td.appendChild(pTag)
  return td
}

// Create Checkbox Function
function createInput(data){
  let td = document.createElement("td")
  td.setAttribute("class","tickBox")
  let inputTag = document.createElement("input")
  inputTag.setAttribute("class","checkbox")
  inputTag.setAttribute("type","checkbox")
  inputTag.setAttribute("name","singleCheckbox")
  inputTag.setAttribute("value",data)
  inputTag.setAttribute("onclick","singleCheckbox(event.target.value,event.target.checked)")
  td.appendChild(inputTag)
  return td
}
