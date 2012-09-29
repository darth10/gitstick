var tags = [];
var tickets = [];
var selectedTags = [];

function addToolbar() {
  var toolbaritem = '<li><a class="roundbutton"><img></a><li>';
  $(toolbaritem).find('a').attr('id', 'addticket').end().find('img').attr('src', 'images/addticket.png').end().appendTo('#toolbar ul');
  $(toolbaritem).find('a').attr('id', 'tags').end().find('img').attr('src', 'images/tags.png').end().appendTo('#toolbar ul');
  $(toolbaritem).find('a').attr('id', 'info').end().find('img').attr('src', 'images/info.png').end().appendTo('#toolbar ul');
};

function isNonEmpty(e) { 
  if (e.length > 0) 
    return true;
  else
    return false;
};

function checkAllTags(e) {
  var selectedNonEmptyTags = selectedTags.filter(isNonEmpty);

  if (selectedNonEmptyTags.length == tags.length) {
    $('#tagsdialog table tr.alltags').find('input[name=alltagscheckbox]').attr('checked', true);
    selectedTags = selectedTags.concat("");
  } else {
    $('#tagsdialog table tr.alltags').find('input[name=alltagscheckbox]').attr('checked', false);
    selectedTags = selectedTags.filter(isNonEmpty);
  }
};

function onAllTagsChanged(e) {
  if ($(this).attr('checked')) {
    $('#tagsdialog table').find('input[name=tagcheckbox]').attr('checked', true);
    selectedTags = tags.slice(0).concat("");
  } else {
    $('#tagsdialog table').find('input[name=tagcheckbox]').attr('checked', false);
    selectedTags = [];
  }

  renderTickets(e);
};

function onTagChanged(e) {
  var tag = $(this).parent().attr('id');
  if ($(this).attr('checked')) {
    selectedTags.push(tag);
  } else {
    var index = selectedTags.indexOf(tag);
    if (index != -1) {
      selectedTags.splice(index, 1);
    }
  }

  checkAllTags(e);
  renderTickets(e);
};

function showTags(e) {
  showProgress('#tagsdialog table', 'tagsProgress');
  setTimeout(renderTags, 0);
};

function renderTags(e) {
  var isEvenRow = false;
  var rowTemplate = '<table><tr><td class="select"><input type="checkbox" name="tagcheckbox"/></td><td class="tag"></td></tr></table>';
  var allRowsTemplate = '<table><tr class="alltags"><td class="select"><input type="checkbox" name="alltagscheckbox"/></td><td class="tag">All</td></tr></table>';

  var addTag = function(t) {
    var rowClass = isEvenRow ? 'even' : 'odd';
    var isChecked = selectedTags.indexOf(t) != -1;
    
    $(rowTemplate).find('tr').attr('class', rowClass).end().find('td.select').attr('id', t).end().find('td.tag').text(t).end().find('input[name=tagcheckbox]').attr('checked', isChecked).end().find('tr').first().appendTo('#tagsdialog table tbody');
    isEvenRow = !isEvenRow;
  };

  $('#tagsdialog table').empty();
  $(allRowsTemplate).find('tr').first().appendTo('#tagsdialog table');
  if (tags.length > 0) {
    var tagsHeader = '<thead><tr><th class="select"></th><th>Select tags</th></tr></thead>';
    $(tagsHeader).appendTo('#tagsdialog table');

    tags.forEach(addTag);
    $('#tagsdialog table').columnFilters({
      excludeColumns : [0]
    });
  }

  checkAllTags(e);
  $('#tagsdialog table tr.alltags').find('input[name=alltagscheckbox]').change(onAllTagsChanged);
  $('#tagsdialog table tr').find('input[name=tagcheckbox]').change(onTagChanged);
};

function onTagsClick(e) {
  var tagsDialog = '<div title="Tags" id="tagsdialog"><table><tbody></tbody></table></div>';
  $(tagsDialog).dialog({
    width : '40%',
    height : 600,
    modal : true,
    draggable : true,
    open : showTags
  });
};

function removeTicketDialog() {
  $('#ticketdialog').remove();
};

function onTicketClick(e) {
  $("#" + this.id).show("pulsate", {
    times : 1
  }, 200);

  var title = $(this).find('td.title').text();
  var ticketId = $(this).attr('id');
  var iFrame = getIFrame(ticketId, 'edit');
  var ticketDialog = '<div title="' + title + '" id="ticketdialog">' + iFrame + '</div>';
  $(ticketDialog).dialog({
    width : '80%',
    height : 700,
    modal : true,
    draggable : true,
    close : removeTicketDialog
  });
};

function checkTags(ticketTags) {
  var toInclude = false;

  if (ticketTags.length == 0)  {
    if(selectedTags.indexOf("") != -1) {
      toInclude = true;
    }
  } else {
    ticketTags.forEach(function(tag) {
      if (selectedTags.indexOf(tag) != -1) {
        toInclude = true;
      }
    });
  };

  return toInclude;
};

function renderTickets(e) {
  var isEvenRow = false;
  var rowTemplate = '<table><tr><td class="title"></td><td class="assigned"></td><td class="time"></td><td class="state"></td><tr><table>';

  $('.ticketstable table').show();
  hideProgress('ticketsProgress');
  var addTicket = function(t) {
    if (checkTags(t['tags'])) {
      var rowClass = isEvenRow ? 'even' : 'odd';
      $(rowTemplate).find('tr').attr('class', rowClass).attr('id', t['name']).end().find('td.title').text(t['title']).end().find('td.assigned').text(t['assigned']).end().find('td.state').attr('class', t['state']).text(t['state']).end().find('td.time').text(t['time']).end().find('tr').first().appendTo('.ticketstable table tbody');
      isEvenRow = !isEvenRow;
    }
  };

  $('.ticketstable table').empty();
  var ticketsHeader = '<thead><tr><th class="title">Title</th><th class="assigned">Assigned To</th><th class="time">Created On</th><th class="state">State</th></tr></thead>';
  $(ticketsHeader).appendTo('.ticketstable table');

  $('<table><tbody></tbody></table>').find('tbody').appendTo('.ticketstable table');
  if (tickets.length > 0) {
    tickets.forEach(addTicket);

    $('.ticketstable table tbody tr').click(onTicketClick);
  }

  if ($('.ticketstable table tr').length != 0) {
    $('.ticketstable table').columnFilters();
  } else {
    $('.ticketstable table').hide();
  }
};

function cacheAndRenderTickets(e) {
  tickets = JSON.parse(e);
  renderTickets(e);
};

function fetchAndRenderTickets() {
  $.ajax({
    type : 'GET',
    url : 'tickets',
    processData : false,
    contentType : 'application/json',
    success : cacheAndRenderTickets
  });
};

function cacheTagsAndLoadTickets(e) {
  tags = JSON.parse(e);
  if (selectedTags.length == 0) {
    selectedTags = tags.slice(0).concat("");
  }

  fetchAndRenderTickets();
};

function loadTagsAndTickets(e, ui) {
  $.ajax({
    type : 'GET',
    url : 'tags',
    processData : false,
    contentType : 'application/json',
    success : cacheTagsAndLoadTickets
  });
};

function onAddTicketClick(e) {
  var iFrame = getIFrame('add', 'add');
  var ticketDialog = '<div title="Add Ticket" id="ticketdialog">' + iFrame + '</div>';
  $(ticketDialog).dialog({
    width : '80%',
    height : 700,
    modal : true,
    draggable : true,
    close : removeTicketDialog
  });
};

function onInfoClick(e) {
  var iFrame = getIFrame('info', 'info');
  var infoDialog = '<div title="Info" id="infodialog">' + iFrame + '</div>';
  $(infoDialog).dialog({
    width : '80%',
    height : 400,
    modal : true,
    draggable : true
  });
};

$(document).ready(function() {
  addToolbar();
  setTooltip('#addticket', 'Add Ticket');
  setTooltip('#tags', 'Tags');
  setTooltip('#info', 'Info');
  showProgress('.ticketstable table', 'ticketsProgress');
  loadTagsAndTickets();

  $('#addticket').click(onAddTicketClick);
  $('#tags').click(onTagsClick);
  $('#info').click(onInfoClick);		  
});
