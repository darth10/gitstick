var tickets = [];
var selectedInfoTags = [];

function nonEmptyTags(tag) {
 if (tag.length == 0) {
   return false;
 } else{
   return true;
 }
};

function onTagsChanged(e, tag) { 
  selectedInfoTags = $('#tags').attr('value').split(',').filter(nonEmptyTags);
  refreshInfo(); 
};

function checkState(ticket, state) {
  if (ticket['state'] == state) {
  	return true;
  } else {
  	return false;
  }
};

function openTickets(ticket) {
  return checkState(ticket, 'open');
};

function resolvedTickets(ticket) {
  return checkState(ticket, 'resolved');
};

function holdTickets(ticket) {
  return checkState(ticket, 'hold');
};

function ignoredTickets(ticket) {
  return checkState(ticket, 'ignored');
};

function taggedTickets(ticket) {
  var ticketTags = ticket['tags'];
  var toInclude = false;

  if (selectedInfoTags.length == 0)  {
      toInclude = true;
  } else {
    selectedInfoTags.forEach(function(tag) {
      toInclude = true;
      if (ticketTags.indexOf(tag) == -1) {
        toInclude = false;
      };
    });
  }

  return toInclude;
};

function resetInfo() {
  var resetText = '...';
  $('#opencount').text(resetText);
  $('#resolvedcount').text(resetText);
  $('#holdcount').text(resetText);
  $('#ignoredcount').text(resetText);
};

function refreshInfo() {
  resetInfo();
  
  var filteredTickets = tickets.filter(taggedTickets); 

  var openCount = filteredTickets.filter(openTickets).length;
  var resolvedCount = filteredTickets.filter(resolvedTickets).length;
  var holdCount = filteredTickets.filter(holdTickets).length;
  var ignoredCount = filteredTickets.filter(ignoredTickets).length;
  
  $('#opencount').text(openCount);
  $('#resolvedcount').text(resolvedCount);
  $('#holdcount').text(holdCount);
  $('#ignoredcount').text(ignoredCount);
};

function cacheTickets(e) {
  tickets = JSON.parse(e);
  refreshInfo();
};

function fetchAndCacheTickets() {
  $.ajax({
    type : 'GET',
    url : 'tickets',
    processData : false,
    contentType : 'application/json',
    success : cacheTickets
  });
};

function checkTickets() {
  if ((window.parent.tickets == undefined) || (window.parent.tickets.length == 0)) {
    fetchAndCacheTickets();
  } else {
    tickets = window.parent.tickets;
    refreshInfo();
  }
};

$(document).ready(function() {
  $('#tags').tagsInput({
    defaultText : 'Add tag',
    width: '98%',
    height: '12px',
    onChange: onTagsChanged
  });
  
  checkTickets();
});
