var open = true;
var user = {};

function addToolbar() {
  var toolbaritem = '<li><a class="roundbutton"><img></a><li>';
  $(toolbaritem).find('a').attr('id', 'save').end().find('img').attr('src', 'images/save.png').end().appendTo('#toolbar ul');
};

function hashOf(n) {
  return CryptoJS.SHA1(n).toString();
};

function toggleInputs(enable) {
  var disabled = !enable;
  $('.settinginput').attr('disabled', disabled);
};

function disableInputsAndShowProgress() {
  toggleInputs(false);
  $('#save').fadeOut('fast');
  showProgress('.saveprogress table', 'settingsProgress');
};

function enableInputsAndHideProgress() {
  toggleInputs(true);
  $('#save').fadeIn('fast');
  hideProgress('settingsProgress');
};

function cacheUser(e) {
  user = JSON.parse(e);

  enableInputsAndHideProgress();
  $('#email').attr('value', user['email']);
  $('#oldpassword').attr('value', '');
  $('#newpassword').attr('value', '');
  $('#confirmnewpassword').attr('value', '');
};

function notifyDoneAndLoadUser(e) {
  notifySuccess('Saved successfully');
  loadAndCacheUser();
};

function notifyErrorAndReset(e) {
  notifyError(e);
  enableInputsAndHideProgress();
};

function notifyServerErrorAndReset(e) {
  notifyError(e);
  enableInputsAndHideProgress();
};

function onSaveClick() {
  disableInputsAndShowProgress();

  var oldPasswordHash = user['password'];
  var salt = user['salt'];
  var date = new Date;
  var newSalt = Math.round(date.getTime() / 1000).toString();

  var newEmail = $('#email').attr('value');
  var password = $('#oldpassword').attr('value');
  var newPassword = $('#newpassword').attr('value');
  var confirmNewPassword = $('#confirmnewpassword').attr('value');

  var newUser = {
    username : user['username'],
    password : user['password'],
    email : newEmail,
    salt : salt
  };

  if (user['password'].length == 0) {
    // user does not exist, in open mode
    newUser['salt'] = newSalt;
    newUser['password'] = hashOf(newSalt + user['username']);
  };

  if (newEmail.length == 0) {
    notifyErrorAndReset('Enter an email address');
    return;
  };

  if ($('#oldpassword').length != 0) {
    var passwordHash = hashOf(salt + password);

    if (passwordHash != oldPasswordHash) {
      notifyErrorAndReset('Invalid password');
      return;
    };

    if (newPassword.length == 0) {
      notifyErrorAndReset('Enter a new password');
      return;
    };

    if (newPassword != confirmNewPassword) {
      notifyErrorAndReset('New passwords do not match');
      return;
    };

    newUser['salt'] = newSalt;
    newUser['password'] = hashOf(newSalt + newPassword);
  };

  $.ajax({
    type : 'PUT',
    url : 'user/' + password,
    processData : false,
    contentType : 'application/json',
    success : notifyDoneAndLoadUser,
    error : notifyServerErrorAndReset,
    data : JSON.stringify(newUser)
  });
};

function loadAndCacheUser() {
  disableInputsAndShowProgress();

  $.ajax({
    type : 'GET',
    url : 'user',
    processData : false,
    contentType : 'application/json',
    success : cacheUser
  });
};

$(document).ready(function() {
  open = ($('#oldpassword').length == 0);

  addToolbar();
  setTooltip('#save', 'Save');
  $('#save').click(onSaveClick);

  loadAndCacheUser();
});
