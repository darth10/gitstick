var ticketId = '';
var ticket = {};
var commentTemplate = '<tr><td class="comment"><div class="text">text</div><br><table width="100%"><tr><td><div class="user">user</div></td><td><div class="time">time</div></td></tr>';
var emptyRow = '<tr height="10px"></tr>';

function notifyUpdateSuccessAndReloadTickets(e) {
  try {
    notifySuccess('Updated ticket successfully');
    renderTicketDetails(e);

    window.parent.loadTagsAndTickets();
  } catch (err) {
    console.log(err);
  }
};

function notifyDeleteSuccessAndClose(e) {
  try {
    window.parent.loadTagsAndTickets();
    window.parent.notifySuccess('Deleted ticket successfully');
    window.parent.removeTicketDialog();
  } catch (err) {
    console.log(err);
    document.location = 'tickets.index';
  }
};

function notifySuccess(mesg) {
  noty({
    text : mesg,
    type : 'success',
    layout : 'topRight'
  });
};

function notifyError(mesg) {
  var error = 'Oops! Something went terribly wrong!';
  if ( typeof (mesg) == 'string') {
    error = mesg;
  } else if ( typeof (mesg) == 'object') {
    if (mesg['statusText'] != undefined) {
      error = mesg['statusText'];
    };
  }

  noty({
    text : error,
    type : 'error',
    layout : 'topRight'
  });
};

function toggleSaveButton(show) {
  if (show) {
    $('#save').fadeIn();
  } else {
    $('#save').fadeOut();
  }
};

function getTagsString() {
  return ticket['tags'].join(',');
};

function getStateToCheck() {
  return $('input[value=' + ticket['state'] + ']');
};

function renderComments() {
  var comments = ticket['comments'];
  var addComment = function(c) {
    $(commentTemplate).find('.text').text(c['comment']).end().find('.user').text(c['user']).end().find('.time').text(c['time']).end().appendTo('#commentstable');
  };

  $('#commentstable').empty();
  comments.forEach(addComment);
};

function renderTicketDetails(e) {
  ticket = JSON.parse(e);

  $('#title').text(ticket['title']);
  $('#createdon').text(ticket['time']);
  $('#assignedto').attr('value', ticket['assigned']);
  $('#tags').importTags(getTagsString());
  getStateToCheck().attr('checked', true);

  renderComments();
};

function fetchAndShowTicket() {
  $.ajax({
    type : 'GET',
    url : 'tickets/' + ticketId,
    processData : false,
    contentType : 'application/json',
    success : renderTicketDetails
  });
};

function renderCommentsAndNotifyDone(e) {
  ticket = JSON.parse(e);
  renderComments();

  notifySuccess('Added comment successfully');
};

function updateComments(newCommentText) {
  $.ajax({
    type : 'POST',
    url : 'tickets/' + ticketId + '/comments',
    processData : false,
    contentType : 'application/json',
    success : renderCommentsAndNotifyDone,
    error : notifyError,
    data : newCommentText
  });

  $('#newcommenttext').attr('value', '');
};

function addCommentAndNotifyDone(e) {
  var newCommentText = $('#newcommenttext').attr('value');
  notifyUpdateSuccessAndReloadTickets(e);

  if (newCommentText.length != 0) {
    updateComments(newCommentText);
  };
};

function onSaveClick(e) {
  var updatedTicket = {
    state : $('.stateinput input').filter(':checked=true').attr('value'),
    tags : $('#tags').attr('value').split(','),
    assigned : $('#assignedto').attr('value'),
    title : ticket['title'],
    name : ticket['name'],
    time : ticket['time'],
    comments : ticket['comments']
  };

  // TODO implement UX and progress
  // TODO disable inputs and show progress

  $.ajax({
    type : 'PUT',
    url : 'tickets/' + ticketId,
    processData : false,
    contentType : 'application/json',
    success : addCommentAndNotifyDone,
    error : notifyError,
    data : JSON.stringify(updatedTicket)
  });
};

function onDeleteClick(e) {
  $.ajax({
    type : 'DELETE',
    url : 'tickets/' + ticketId,
    processData : false,
    contentType : 'application/json',
    success : notifyDeleteSuccessAndClose,
    error : notifyError
  });
};

$(document).ready(function() {
  ticketId = $('body').find('table').first().attr('id');

  setTooltip('#savebutton', 'Save');
  setTooltip('#deletebutton', 'Delete ticket');
  $('#tags').tagsInput({
    defaultText : '',
    width : '95%',
    height : '60px'
  });

  $('#savebutton').click(onSaveClick);
  $('#deletebutton').click(onDeleteClick);

  fetchAndShowTicket();
});
