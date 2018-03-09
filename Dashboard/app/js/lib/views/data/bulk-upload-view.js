/*global Resumable, FLOW, $, Ember */

FLOW.uuid = function (file) {
  return Math.uuidFast();
};

FLOW.uploader = Ember.Object.create({
  showDragAction: false,
  startUploadingAction: false,
  endDragAction: false,
  droppedAction: false,
  r: new Resumable({
    target: FLOW.Env.flowServices + '/upload',
    uploadDomain: FLOW.Env.surveyuploadurl.split('/')[2],
    simultaneousUploads: 1,
    testChunks: false,
    throttleProgressCallbacks: 1, // 1s
    chunkRetryInterval: 1000, // 1s
    chunkSize: 512 * 1024, // 512KB,
    generateUniqueIdentifier: FLOW.uuid
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

  isUploading: function () {
    return this.get('r').isUploading();
  },

  cancel: function () {
    return this.get('r').cancel();
  },

  addFile: function (file) {
    return this.get('r').addFile(file);
  },

  registerEvents: function () {
    var r = this.get('r');

    // Handle file add event
    r.on('fileAdded', function (file) {
      var li;

      FLOW.uploader.set('cancelled', false);

      // Show progress pabr
      $('.resumable-progress, .resumable-list').show();
      // Show pause, hide resume
      // $('.resumable-progress .progress-resume-link').hide();
      // $('.resumable-progress .progress-pause-link').show();
      // Add the file to the list
      li = $('.resumable-file-' + file.uniqueIdentifier);
      if (li.length === 0) {
        $('.resumable-list').append('<li class="resumable-file-' + file.uniqueIdentifier + '">Uploading <span class="resumable-file-name"></span> <span class="resumable-file-progress"></span>');
      }
      $('.resumable-file-' + file.uniqueIdentifier + ' .resumable-file-name').html(file.fileName);
      $('.progress-bar').css({
        width: '0%'
      });
      // Actually start the upload
      r.upload();
    });

    r.on('pause', function () {
      // Show resume, hide pause
      $('.resumable-progress .progress-resume-link').show();
      $('.resumable-progress .progress-pause-link').hide();
    });
    
    r.on('uploadStart', function(){
      //show the drop action border when upload starts
       //FLOW.uploader.set('showDragAction', true); got it wrong... listen to dragEnter n dragLeave
        FLOW.uploader.set('startUploadingAction', true)
    })

    r.on('complete', function () {
      // Hide pause/resume when the upload has completed
      $('.resumable-progress .progress-resume-link, .resumable-progress .progress-pause-link').hide();
      if (!FLOW.uploader.get('cancelled')) {
        FLOW.uploader.showCompleteMessage();
      }
      //Hide the drag action border after uploading
       FLOW.uploader.set('showDragAction', false); //got the stuff wrong listen to dragEnter n dragLeave
    });

    r.on('fileSuccess', function (file, message) {
      var target = this.opts.target,
      data = {
        uniqueIdentifier: file.uniqueIdentifier,
        filename: file.fileName,
        baseURL: location.protocol + '//' + location.host,
        appId: FLOW.Env.appId,
        uploadDomain: this.opts.uploadDomain,
        complete: true
      },
      fname = file.fileName,
      excel = /\.xlsx$/gi,
      csv = /\.csv$/gi,
      sc;

      if (excel.test(fname)) {
        data.surveyId = FLOW.selectedControl.selectedSurvey.get('id');
      }

      if (csv.test(fname)) {
        sc = FLOW.selectedControl.selectedCascadeResource;
        data.cascadeResourceId = sc.get('keyId');
        data.numLevels = FLOW.selectedControl.get('cascadeImportNumLevels');
        data.includeCodes = !!FLOW.selectedControl.get('cascadeImportIncludeCodes');
      }

      // Reflect that the file upload has completed
      $('.resumable-file-' + file.uniqueIdentifier + ' .resumable-file-progress').html('(' + Ember.String.loc('_upload_complete') + ')');

      setTimeout(function() {
        $.ajax({
          url : target,
          cache : false,
          type : 'POST',
          data : data
        });
      }, 500);

    });

    r.on('fileError', function (file, message) {
      // Reflect that the file upload has resulted in error
      $('.resumable-file-' + file.uniqueIdentifier + ' .resumable-file-progress').html('(file could not be uploaded: ' + message + ')');
    });

    r.on('fileProgress', function (file) {
      // Handle progress for both the file and the overall upload
      $('.resumable-file-' + file.uniqueIdentifier + ' .resumable-file-progress').html(Math.floor(file.progress() * 100) + '%');
      $('.progress-bar').css({
        width: Math.floor(r.progress() * 100) + '%'
      });
    });

  },
  showCancelledMessage: function () {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_upload_cancelled'));
    FLOW.dialogControl.set('message', Ember.String.loc('_upload_cancelled_due_to_navigation'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },
  showCompleteMessage: function () {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_upload_complete'));
    FLOW.dialogControl.set('message', Ember.String.loc('_upload_complete_message'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  }
});

FLOW.BulkUploadAppletView = FLOW.View.extend({

  didInsertElement: function () {
    FLOW.uploader.assignDrop($('.resumable-drop')[0]);
    FLOW.uploader.assignBrowse($('.resumable-browse')[0]);
    FLOW.uploader.registerEvents();
    
    //listening to some  drag n drop events.
    $('.resumable-drop').on('dragenter', function(evt){
       evt.preventDefault();
       console.log('started dragging')
       FLOW.uploader.set('showDragAction', true)
    })
    
    $('.resumable-drop').on('dragleave', function(evt){
       evt.preventDefault();
       console.log('ended the dragging')
        //this.set('endDragAction', true)
        //FLOW.uploader.set('endDragAction', true)
        FLOW.uploader.set('showDragAction', false)
    })
    
    $('.resumable-drop').on('drop', function(evt) {
      evt.preventDefault();
      console.log('dropping something!!!')
      //this.set('droppedAction', true)
      //FLOW.uploader.set('droppedAction', true) 
      FLOW.uploader.set('showDragAction', true) 
    })
    
  },
  willDestroyElement: function () {
    FLOW.uploader.set('cancelled', FLOW.uploader.isUploading());
    FLOW.uploader.cancel();
    this._super();
  },
  //dragActionStarted: false,
  
  
  //listening to events when file is dragged here.
/*  dragEnter: function (evt) {
    evt.preventDefault();
    //console.log(evt.target)
    if (!this.showDragAction) {
      console.log('starting to drag files, set new style')
      //call function to set style
      this.set('showDragAction',true);
    }
  },
  dragOver: function (evt) {
     evt.preventDefault();
     if (!this.dragActionStarted) {
       console.log('passing over!!!');
       this.set('dragActionStarted', true);
     }
  },
  dragLeave: function (evt) {
    evt.preventDefault();
    console.log('iam leaving the valid drop point');
    this.set('dragActionStarted', false);
    //revert back to the original border color when user hovers the file away from drop point
       if (!this.startUploadingAction) {
         console.log('Left drag zone without uploading, revert style') //somehow but still buggy
         //call function to revert style
         this.set('showDragAction', false);
      }
  }*/
  
});

/* Show warning when trying to close the tab/window with an upload process in progress */
window.onbeforeunload = function (e) {
  var confirmationMessage = Ember.String.loc('_upload_in_progress');

  if (FLOW.uploader.isUploading()) {
    (e || window.event).returnValue = confirmationMessage;
    return confirmationMessage;
  }
};
