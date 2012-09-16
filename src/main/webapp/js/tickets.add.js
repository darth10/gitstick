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

function notifySuccessAndRedirect(e) {
  var newTicket = JSON.parse(e);
  var newTicketId = newTicket['name'];

  notifySuccess('Added ticket successfully');

  try {
    window.parent.loadTagsAndTickets();
    document.location = newTicketId;
  } catch (err) {
    console.log(err);
    document.location = newTicketId;
  }
};

function isTag(e) {
  if (e.length > 0) {
  	return true;
  } else {
	return false;
  }
};

function onSaveClick(e) {
  // TODO implement add ticket JS
  var newComment = [];
  var newCommentText = $('#newcommenttext').attr('value');
  if (newCommentText.length != 0) {
    newComment.push({
      user : "",
      time : "",
      comment : newCommentText
    });
  };

  var newTags = $('#tags').attr('value').split(',').filter(isTag);
  var newTicket = {
    state : $('.stateinput input').filter(':checked=true').attr('value'),
    tags : newTags,
    assigned : $('#assignedto').attr('value'),
    title : $('#title').attr('value'),
    name : "",
    time : "",
    comments : newComment
  };

  $.ajax({
    type : 'POST',
    url : 'tickets',
    processData : false,
    contentType : 'application/json',
    success : notifySuccessAndRedirect,
    error : notifyError,
    data : JSON.stringify(newTicket)
  });
};

$(document).ready(function() {
  setTooltip('#savebutton', 'Save');

  $('#tags').tagsInput({
    defaultText : '',
    width : '95%',
    height : '60px'
  });

  $('#savebutton').click(onSaveClick);
});
