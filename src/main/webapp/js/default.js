function setTooltip(id, text) {
  var element = $(id);
  if ( typeof (element.simpletip) != 'undefined') {
    element.simpletip({
      content : text,
      fixed : false
    });
  }
};

function getIFrame(src, name) {
  return '<iframe src="' + src + '" name="' + name + '" frameborder="0" width="99%" height="100%"></iframe>';
};

function showProgress(id, progressName) {
  $(id).find('tbody').empty();
  var progress = '<tbody><tr id="' + progressName + '" width="100%"><td style="text-align: center;"><img src="images/progress.gif"/></td></tr></tbody>';
  $(progress).find('tr').first().appendTo(id);
};

function hideProgress(progressName) {
  $('#' + progressName).remove();
};

function notifyInfo(mesg) {
  noty({
    text : mesg,
    layout : 'topLeft'
  });
};

function notifySuccess(mesg) {
  noty({
    text : mesg,
    type : 'success',
    layout : 'topLeft'
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
    layout : 'topLeft'
  });
};

$(document).ready(function() {
  setTooltip('#tickets', 'Tickets');
  setTooltip('#log', 'Log');
  setTooltip('#settings', 'Settings');
});
