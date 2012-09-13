function renderLog(e) {
  var commits = JSON.parse(e);
  var isEvenRow = false;
  var rowTemplate = '<table><tr><td class="message"></td><td class="time"></td><td class="author"></td><tr><table>';

  hideProgress('logProgress');
  var addCommit = function(t) {
    var rowClass = isEvenRow ? 'even' : 'odd';
    $(rowTemplate).find('tr').attr('class', rowClass).end().find('td.message').text(t['message']).end().find('td.time').text(t['time']).end().find('td.author').text(t['author'] + " [" + t['email'] + "]").end().find('tr').first().appendTo('.logtable table tbody');
    isEvenRow = !isEvenRow;
  }
  var logHeader = '<thead><tr><td class="message">Message</td><td class="time">Time</td><td class="author">Author</td><tr><thead>';
  $(logHeader).appendTo('.logtable table');

  commits.forEach(addCommit);
  $('.logtable table').columnFilters();
};

function fetchAndRenderLog() {
  showProgress('.logtable table', 'logProgress');
  $.ajax({
    type : 'GET',
    url : 'log',
    processData : false,
    contentType : 'application/json',
    success : renderLog
  });
};

$(document).ready(function() {
  fetchAndRenderLog();
});
