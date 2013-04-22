/*global Resumable, FLOW, $, Ember */

FLOW.uploader = Ember.Object.create({
  r: new Resumable({
    target: FLOW.Env.flowServices + '/upload',
    uploadDomain: FLOW.Env.surveyuploadurl.split('/')[2],
    simultaneousUploads: 4,
    testChunks: false,
    throttleProgressCallbacks: 1, // 1s
    chunkRetryInterval: 1000 // 1s
  }),

  assignDrop: function (el) {
    return this.get('r').assignDrop(el);
  },

  assignBrowse: function (el) {
    return this.get('r').assignBrowse(el);
  },

  support: function () {
    return this.get('r').support;
  }.property(),

  upload: function () {
    return this.get('r').upload();
  },

  pause: function () {
    return this.get('r').pause();
  },

  registerEvents: function () {
    var r = this.get('r');

 // Handle file add event
    r.on('fileAdded', function(file){
        // Show progress pabr
        $('.resumable-progress, .resumable-list').show();
        // Show pause, hide resume
        // $('.resumable-progress .progress-resume-link').hide();
        // $('.resumable-progress .progress-pause-link').show();
        // Add the file to the list
        $('.resumable-list').append('<li class="resumable-file-'+file.uniqueIdentifier+'">Uploading <span class="resumable-file-name"></span> <span class="resumable-file-progress"></span>');
        $('.resumable-file-'+file.uniqueIdentifier+' .resumable-file-name').html(file.fileName);
        $('.progress-bar').css({width: '0%'});
        // Actually start the upload
        r.upload();
      });

    r.on('pause', function(){
        // Show resume, hide pause
        $('.resumable-progress .progress-resume-link').show();
        $('.resumable-progress .progress-pause-link').hide();
      });

    r.on('complete', function(){
        // Hide pause/resume when the upload has completed
        $('.resumable-progress .progress-resume-link, .resumable-progress .progress-pause-link').hide();
      });

    r.on('fileSuccess', function(file,message){
        // Reflect that the file upload has completed
        $('.resumable-file-'+file.uniqueIdentifier+' .resumable-file-progress').html('(completed)');
        $.ajax({
          url: this.opts.target,
          cache: false,
          type: 'POST',
          data: {
            uniqueIdentifier: file.uniqueIdentifier,
            filename: file.fileName,
            baseURL: location.protocol + '//' + location.host,
            uploadDomain: this.opts.uploadDomain
          }
        });
      });

    r.on('fileError', function(file, message){
        // Reflect that the file upload has resulted in error
        $('.resumable-file-'+file.uniqueIdentifier+' .resumable-file-progress').html('(file could not be uploaded: '+message+')');
      });

    r.on('fileProgress', function(file){
        // Handle progress for both the file and the overall upload
        $('.resumable-file-'+file.uniqueIdentifier+' .resumable-file-progress').html(Math.floor(file.progress()*100) + '%');
        $('.progress-bar').css({width:Math.floor(r.progress()*100) + '%'});
      });
  }
});

FLOW.BulkUploadAppletView = FLOW.View.extend({
  didInsertElement: function () {
    FLOW.uploader.assignDrop($('.resumable-drop')[0]);
    FLOW.uploader.assignBrowse($('.resumable-browse')[0]);
    FLOW.uploader.registerEvents();
  }
});

window.onbeforeunload = function (e) {
  var confirmationMessage = Ember.String.loc('_upload_in_progress'),
      r = FLOW.uploader.r;

  if(r.files.length > 0 && r.progress() < 1) {
    (e || window.event).returnValue = confirmationMessage;
    return confirmationMessage;
  }
};