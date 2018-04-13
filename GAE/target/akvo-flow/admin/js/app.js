
loader.register('akvo-flow/templates/application/application-public', function(require) {

return Ember.Handlebars.compile("<div class=\"loadSave\">\n      {{#if FLOW.savingMessageControl.areSavingBool}}<div class=\"isSaving\">{{t _saving}}</div>\n      {{else}}\n        {{#if FLOW.savingMessageControl.areLoadingBool}}<div class=\"isLoading\">{{t _loading}}</div>\n        {{/if}}\n      {{/if}}\n  </div>\n  <header class=\"floats-in top\" id=\"header\" role=\"banner\">\n      <div class=\"widthConstraint\">\n            <h1>Akvo Flow</h1>\n            <ul>\n               <li class=\"logIn\"><a href=\"/admin/\"  class=\"smallButton\">{{t _log_in}}</a></li>\n            </ul>\n      </div>\n  </header>\n    <div id=\"pageWrap\" class=\"widthConstraint belowHeader public\">\n        {{outlet}}\n</div>\n\n   {{view FLOW.FooterView}}");

});

loader.register('akvo-flow/templates/application/application', function(require) {

return Ember.Handlebars.compile("<div class=\"loadSave\">\n  {{#if FLOW.savingMessageControl.areSavingBool}}\n    <div class=\"isSaving\">{{t _saving}}</div>\n  {{else}}\n    {{#if FLOW.savingMessageControl.areLoadingBool}}\n      <div class=\"isLoading\">{{t _loading}}</div>\n    {{/if}}\n  {{/if}}\n</div>\n<header class=\"floats-in top\" id=\"header\" role=\"banner\">\n  <div>\n    <h1>Akvo Flow</h1>\n    <nav id=\"topnav\" role=\"navigation\" class=\"appNav\">\n      {{view FLOW.NavigationView controllerBinding=\"controller.controllers.navigationController\"}}\n    </nav>\n    {{view FLOW.HeaderView}}\n  </div>\n</header>\n{{outlet}}\n<div><iframe id=\"downloader\" style=\"display:none\" width=\"0\" height=\"0\"></iframe></div>\n<div {{bindAttr class=\"FLOW.dialogControl.showDialog:display :overlay\"}}>\n  <div class=\"blanket\"></div>\n  <div class=\"dialogWrap\">\n    <!-- the dialog contents -->\n    <div class=\"confirmDialog dialog\">\n      <h2>{{FLOW.dialogControl.header}}</h2>\n      <p class=\"dialogMsg\">{{FLOW.dialogControl.message}}</p>\n      <br/><br/>\n      <div class=\"buttons menuCentre\">\n        <ul>\n          {{#if FLOW.dialogControl.showOK}} <li><a {{action \"doOK\" target=\"FLOW.dialogControl\"}} class=\"ok smallBtn\">{{t _ok}}</a></li>{{/if}}\n          {{#if FLOW.dialogControl.showCANCEL}} <li><a {{action \"doCANCEL\" target=\"FLOW.dialogControl\"}}} class=\"cancel\">{{t _cancel}}</a></li>{{/if}}\n        </ul>\n      </div>\n    </div>\n  </div>\n</div>\n{{view FLOW.FooterView}}\n");

});

loader.register('akvo-flow/templates/application/footer-public', function(require) {

return Ember.Handlebars.compile(" <footer class=\"floats-in bottomPage smaller\" role=\"contentinfo\">\n    <div class=\"widthConstraint\">\n\t  <nav id=\"footerNav\" class=\"floats-in footItems\">\n\t\t<ul>\n\t\t\t<li class=\"footLink\"><a href=\"https://github.com/akvo/akvo-flow/releases\" title=\"Go to Software Updates\" target=\"_blank\" > {{t _software_updates}}</a></li>\n\t\t\t<li class=\"footLink\"><a href=\"http://flowsupport.akvo.org/\" title=\"Help and Support\" target=\"_blank\" > {{t _help_support}}</a></li>\n\t\t\t<li class=\"footLink\"><a href=\"http://akvo.org/help/akvo-policies-and-terms-2/akvo-flow-terms-of-use/\" title=\"Terms of Service\" target=\"_blank\" >{{t _terms_of_service}}</a></li>\n\t\t</ul>\n\t</nav>\n  </div>\n  <div><small>{{t _copyright}} &copy; 2012-2018 <a href=\"http://www.akvo.org\" title=\"akvo.org\" target=\"_blank\">akvo.org</a></small></div>\n</footer>\n");

});

loader.register('akvo-flow/templates/application/footer', function(require) {

return Ember.Handlebars.compile(" <footer class=\"floats-in bottomPage smaller\" role=\"contentinfo\">\n    <div class=\"widthConstraint\">\n\t  <nav id=\"footerNav\" class=\"floats-in footItems\">\n\t\t<ul>\n\t\t\t<li class=\"footLink\"><a href=\"/app2\" target=\"_blank\">{{t _download_flow_app}}</a></li>\n\t\t\t<li class=\"footLink\"><a href=\"https://github.com/akvo/akvo-flow/releases\" title=\"Go to Software Updates\" target=\"_blank\" > {{t _software_updates}}</a></li>\n\t\t\t<li class=\"footLink\"><a href=\"http://flowsupport.akvo.org/\" title=\"Help and Support\" target=\"_blank\" > {{t _help_support}}</a></li>\n\t\t\t<li class=\"footLink\"><a href=\"http://akvo.org/help/akvo-policies-and-terms-2/akvo-flow-terms-of-use/\" title=\"Terms of Service\" target=\"_blank\" >{{t _terms_of_service}}</a></li>\n\t\t</ul>\n\t\t<ul>\n\t\t\t<li>\n\t\t\t\t<form>\n\t\t\t\t   <label class=\"languageSelector\">\n\t\t\t\t   {{view Ember.Select\n\t\t\t\t      contentBinding=\"FLOW.dashboardLanguageControl.content\"\n\t\t\t\t      optionLabelPath=\"content.label\"\n\t\t\t\t      optionValuePath=\"content.value\"\n\t\t\t\t      valueBinding=\"FLOW.dashboardLanguageControl.dashboardLanguage\" }}\n\t\t\t\t   </label>\n\t\t\t\t</form>\n\t\t\t</li>\n\t\t</ul>\n\t</nav>\n  </div>\n  <div><small>__VERSION__ - {{t _copyright}} &copy; 2012-2018 <a href=\"http://www.akvo.org\" title=\"akvo.org\" target=\"_blank\">akvo.org</a></small></div>\n <footer>\n");

});

loader.register('akvo-flow/templates/application/header-common', function(require) {

return Ember.Handlebars.compile("<li class=\"logOut\"><a href=\"/admin/logout.html\" class=\"smallButton\">{{t _log_out}}</a></li>\n");

});

loader.register('akvo-flow/templates/application/navigation', function(require) {

return Ember.Handlebars.compile("<ul class=\"floats-in\">\n  {{#view view.NavItemView item=\"navSurveys\" }}\n    <a {{action doNavSurveys}}>{{t _surveys}}</a>\n  {{/view}}\n  {{#view view.NavItemView item=\"navDevices\" }}\n\t{{#if view.showDevicesButton}}\n    <a {{action doNavDevices}}>{{t _devices}}</a>\n\t{{/if}}\n  {{/view}}\n\t{{#view view.NavItemView item=\"navData\" }}\n    <a {{action doNavData}}>{{t _data}}</a>\n  {{/view}}\n\t{{#view view.NavItemView item=\"navReports\" }}\n    <a {{action doNavReports}}>{{t _reports}}</a>\n  {{/view}}\n  {{#if view.showMapsButton}}\n  {{#view view.NavItemView item=\"navMaps\" }}\n    <a {{action doNavMaps}}>{{t _maps}}</a>\n  {{/view}}\n  {{/if}}\n  {{#if FLOW.currentUser.showUserTab }}\n\t  {{#view view.NavItemView item=\"navUsers\" }}\n      <a {{action doNavUsers}}>{{t _users}}</a>\n    {{/view}}\n  {{/if}}\n  {{#view view.NavItemView item=\"navMessages\" }}\n    <a {{action doNavMessages}}>{{t _messages}}</a>\n  {{/view}}\n</ul>\n");

});

loader.register('akvo-flow/templates/navAdmin/nav-admin', function(require) {

return Ember.Handlebars.compile("");

});

loader.register('akvo-flow/templates/navData/applets/bulk-import-applet', function(require) {

return Ember.Handlebars.compile("<div class=\"block\">\n<applet height=\"30\" width=\"100\"\n\tcode=\"com.gallatinsystems.framework.dataexport.applet.DataImportAppletImpl\"\n\tarchive=\"../exporterapplet.jar,../gdata-core-1.0.jar\">\n\t<param name=\"importType\" value=\"BULK_SURVEY\">\n\t<param name=\"selectionMode\" value=\"dir\">\n\t<param name=\"java_arguments\" value=\"-Xmx1024m\">\n\t<param name=\"serverOverride\" value=\"{{getServer}}\">\n\t<param name=\"factoryClass\"\n\t\tvalue=\"org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory\">\n</applet>\n</div>");

});

loader.register('akvo-flow/templates/navData/applets/raw-data-import-applet', function(require) {

return Ember.Handlebars.compile("<div class=\"block\">\n<applet height=\"30\" width=\"100\"\n\tcode=\"com.gallatinsystems.framework.dataexport.applet.DataImportAppletImpl\"\n\tarchive=\"../exporterapplet.jar,../json.jar,../jcommon-1.0.16.jar,../jfreechart-1.0.13.jar,../poi-3.8-20120326.jar,../poi-ooxml-3.8-20120326.jar,../poi-ooxml-schemas-3.8-20120326.jar,../xbean.jar,../dom4j-1.6.1.jar,../gdata-core-1.0.jar\">\n\t<param name=\"cache-archive\"\n\t\tvalue=\"exporterapplet.jar,json.jar,poi-3.5-signed.jar\">\n\t<param name=\"cache-version\" value=\"1.3,1.0,3.5\">\n\t<param name=\"factoryClass\"\n\t\tvalue=\"org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory\">\n\t<param name=\"java_arguments\" value=\"-Xmx512m\">\n\t<param name=\"importType\" value=\"RAW_DATA\">\n\t<param name=\"serverOverride\" value=\"{{getServer}}\">\n\t<param name=\"criteria\" value=\"k:=test;surveyId:={{unbound FLOW.selectedControl.selectedSurvey.keyId}}\">\n</applet>\n</div>");

});

loader.register('akvo-flow/templates/navData/bulk-upload', function(require) {

return Ember.Handlebars.compile("<section class=\"fullWidth reportTools\" id=\"reportBlocks\">\n  {{#view FLOW.BulkUploadAppletView}}\n    <div id=\"gdprSwitch\" style=\"display : block;\">\n      <h3>{{t _gdpr_wakthrough_title}}</h3>  \n      <a onClick=\"showHideDiv('gdprSwitch');return false;\" href=\"#\" id=\"gdprBtn\" class=\"hidden\">close</a>\n      <ul class=\"gdprSwitchSteps\">\n        <li class=\"gdprStep\">\n            <div class=\"innerFrame gdpr01\">\n              <div class=\"wrap\">\n                <img src=\"images/gdpr01.svg\">     \n              </div>               \n            </div>\n              <h3>{{t _locate_the_data}}</h3>\n              <p>{{t _gdpr_walkthrough_step1}}</p>\n        </li>\n        <li class=\"gdprStep\">\n            <div class=\"innerFrame gdpr02\">\n              <div class=\"wrap\">\n                <img src=\"images/gdpr02.svg\">     \n              </div>      \n            </div>\n              <h3>{{t _copy_the_data}}</h3>\n              <p>{{t _gdpr_walkthrough_step2}}</p>\n        </li>\n        <li class=\"gdprStep\">\n            <div class=\"innerFrame gdpr03\">\n                <div class=\"wrap\">\n                  <img src=\"images/gdpr03.svg\">     \n                </div>       \n              </div>\n              <h3>{{t _upload_zip}}</h3>\n        </li>\n      </ul>\n      <script type=\"text/javascript\">\n        window.onresize = function(){\n          $('.innerFrame img').css({\n            maxWidth: $('.innerFrame').width() * 0.9,\n            maxHeight: $('.innerFrame').height() * 0.9,\n          });\n        };s\n        function showHideDiv(ele) {\n          var srcElement = document.getElementById(ele);\n          if (srcElement != null) {\n            if (srcElement.style.display == \"block\") {\n              srcElement.style.display = 'none';\n              document.getElementById('gdprBtn').innerHTML = 'open';\n            }\n            else {\n              srcElement.style.display = 'block';\n              document.getElementById('gdprBtn').innerHTML = 'close';\n            }\n            return false;\n          }\n        }\n      </script>\n    </div>  \n    <div class=\"bulkUpload\">\n      {{#if FLOW.uploader.support}}\n        <div class=\"resumable-drop\" ondragenter=\"jQuery(this).addClass('resumable-dragover');\" ondragend=\"jQuery(this).removeClass('resumable-dragover');\" ondrop=\"jQuery(this).removeClass('resumable-dragover');\">\n          <div class=\"dragTxt\">\n            <div class=\"zipIcn\"></div>\n            <p>{{t _drop_files}} <a class=\"resumable-browse\"><u>{{t _select_files}}</u></a></p>\n          </div>\n          <ul class=\"resumable-list\"></ul>\n        </div>\n      {{else}}\n        <div class=\"resumable-error\">\n          {{t _bulk_upload_unsupported_browser}}\n        </div>\n      {{/if}}\n    </div>\n{{/view}}\n</section>");

});

loader.register('akvo-flow/templates/navData/cascade-resources', function(require) {

return Ember.Handlebars.compile("{{#view FLOW.CascadeResourceView}}\n<section class=\"fullWidth cascadeWrap\">\n\n<section class=\"cascadeControls\">\n    <nav class=\"doubleMenu\">\n        <ul class=\"\">\n            <li>\n             <label class=\"selectinLabel dependencySelect\"> {{t _choose_existing_cascade}}\n        \t{{view Ember.Select\n\t\t\tcontentBinding=\"FLOW.cascadeResourceControl.arrangedContent\"\n        \tselectionBinding=\"FLOW.selectedControl.selectedCascadeResource\"\n        \toptionLabelPath=\"content.name\"\n        \toptionValuePath=\"content.keyId\"\n            prompt=\"\"\n        \tpromptBinding=\"Ember.STRINGS._select_cascade\"}}</label>\n            </li>\n        </ul>\n    </nav>\n    <nav class=\"menuTopbar doubleMenu\">\n        <ul>\n            <li class=\"addCascade\">\n               {{#if view.showNewCascadeField}}\n        \t\t\t{{view Ember.TextField valueBinding=\"view.cascadeResourceName\"}}\n          \t\t\t<a {{action \"saveNewCascadeResource\" target=\"this\"}} class=\"smallBtn\">{{t _save}}</a>\n          \t\t\t<a {{action \"cancelNewCascadeResource\" target=\"this\"}}>{{t _cancel}}</a>\n      \t\t\t{{else}}\n      \t\t\t\t<a {{action \"newCascade\" target=\"this\"}} class=\"button\">{{t _add_new_cascade}}</a>\n      \t\t\t{{/if}}\n            </li>\n        </ul>\n    </nav>\n</section>\n<div class=\"floats-in \">\n  <section id=\"main\" class=\"cascadeSection middleSection floats-in\" role=\"main\">\n        \t{{#if view.oneSelected}}\n            <section id=\"\" class=\"cascadeDetailsPanel floats-in\">\n                <h2>{{FLOW.selectedControl.selectedCascadeResource.name}}</h2>\n                <ul class=\"cascadeAction\">\n                  <li><a {{action \"publishResource\" target=\"this\"}} class=\"saveNewSurvey\">{{t _publish}}</a></li>\n                  <li><a {{action confirm FLOW.dialogControl.delCR target=\"FLOW.dialogControl\"}} class=\"deleteSurvey\">{{t _delete}}</a></li>\n                  <li class=\"offSet\"><a {{action \"showImportCascade\" target=\"this\"}} class=\"importBtn\">{{t _import_cascade_data}}</a></li>                    \n                  <li class=\"cascadeInfo\">{{t _cascading_levels}}<span class=\"cascadesN\">{{FLOW.selectedControl.selectedCascadeResource.numLevels}}</span>\n                    </li>\n                   <li class=\"cascadeInfo\">{{t _status}}\n                    <span {{bindAttr class=\":cascadeStatus FLOW.cascadeResourceControl.isPublished:published:unpublished\"}}>{{FLOW.cascadeResourceControl.currentStatus}}</span>\n                    </li>\n                </ul>                \n             </section>\n                <section {{bindAttr class=\"view.showImportDialog:display:hidden  :importBlock\"}}>\n\t\t\t\t\t<div>\n\t\t\t\t\t\t<h4>{{t _import_cascade_file}}</h4>\n\t\t\t\t\t\t<fieldset>\n\t\t\t\t\t\t{{#if FLOW.uploader.support}}\n\t\t\t\t\t\t<label>{{t _cascade_import_number_of_levels}}</label>\n\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"FLOW.selectedControl.cascadeImportNumLevels\" placeholderBinding=\"Ember.STRINGS._cascade_import_num_levels\"}}\n\t\t\t\t\t\t</fieldset>\n\t\t\t\t\t\t<fieldset>\n\t\t\t\t\t\t<label className=\"checkboxType\">{{t _cascade_import_code_legend}}</label>\n\t\t\t\t\t\t{{view Ember.Checkbox checkedBinding=\"FLOW.selectedControl.cascadeImportIncludeCodes\"}}\n\t\t\t\t\t\t</fieldset>\n\t\t\t\t\t\t<fieldset>\n\t\t\t\t\t\t<input id=\"cascade-resource-file\" type=\"file\" /></label>\n\t\t\t\t\t\t<a {{action \"importFile\" target=\"this\" }} class=\"smallBtn\">{{t _import_cascade_file}}</a>\n\t\t\t\t\t\t<a {{action \"hideImportCascade\" target=\"this\" }} class=\"\">{{t _cancel}}</a>\n\t\t\t\t\t\t<div>\n\t\t\t\t\t\t\t<p>\n\t\t\t\t\t\t\t\t{{t _import_help_text}}\n\t\t\t\t\t\t\t</p>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t\t<div class=\"resumable-progress\">\n\t\t\t\t\t\t\t<h5>{{t _progress}}</h5>\n\t\t\t\t\t\t\t<div class=\"progress-container\">\n\t\t\t\t\t\t\t\t<div class=\"progress-bar\"></div>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t\t<ul class=\"resumable-list\"></ul>\n\t\t\t\t\t\t{{else}}\n\t\t\t\t\t\t<div class=\"resumable-error\">{{t\n\t\t\t\t\t\t\t_bulk_upload_unsupported_browser}}</div>\n\t\t\t\t\t\t{{/if}}\n\t\t\t\t\t</div>\n\t\t\t\t</section>\n                <section id=\"\" {{bindAttr class=\"view.showImportDialog:hidden:display  :cascadeDetailsPanel\"}}>\n\n            </section>\n            <section {{bindAttr class=\"view.showImportDialog:hidden:display  :cascadeItems\"}}>\n                <nav class=\"cascadeBreadCrumb\">\n\t\t\t\t\t<ul>\n\t\t\t\t\t\t{{#each item in FLOW.cascadeResourceControl.levelNames}}\n\t\t\t\t\t\t\t{{#view FLOW.CascadeLevelBreadcrumbView contentBinding=\"item\"}}\n\t\t\t\t\t\t\t\t<a {{action \"adaptColView\" target=\"this\"}}><span>{{item.level}}</span>{{item.levelName}}</a>\n\t\t\t\t\t\t\t{{/view}}\n\t\t\t\t\t\t{{/each}}\n\t\t\t\t\t</ul>\n\t\t\t\t\t<div class=\"addLevelBtn\"><a {{action \"addLevel\" target=\"this\"}} class=\"smallButton\">{{t _add_level}}</a></div>\n\t            </nav>\n\t        </section>\n\t\t\t<section {{bindAttr class=\"view.showImportDialog:hidden:shown  :levelColumns :floats-in\"}}>\n                    <div class=\"levelCol level01\">\n                    \t{{#view FLOW.CascadeLevelNameView col=1 origLevelName=FLOW.cascadeResourceControl.displayLevelName1}}\n                       \t\t<span class=\"levelNbr\">Level {{FLOW.cascadeResourceControl.displayLevelNum1}}</span>\n                        \t<fieldset class=\"levelHead\">\n                        \t\t{{#if view.editFieldVisible}}\n\t\t\t\t \t\t\t\t\t{{view Ember.TextField valueBinding=\"view.levelName\"}}\n\t          \t\t\t\t\t\t\t<a {{action \"saveNewLevelName\" target=\"this\"}} class=\"smallBtn\">{{t _save}}</a>\n\t          \t\t\t\t\t\t\t<a {{action \"cancelNewLevelName\" target=\"this\"}}>{{t _cancel}}</a>\n\t\t\t\t\t\t\t\t{{else}}\n\t\t\t\t\t\t\t\t\t<label for=\"level01Name\">{{FLOW.cascadeResourceControl.displayLevelName1}}\n\t\t\t\t\t\t\t\t\t<a {{action \"showEditField\" target=\"this\"}} class=\"editSurvey\">{{t _edit}}</a></label>\n\t\t\t\t\t\t\t\t{{/if}}\n                           \t \t{{#view FLOW.CascadeNodeView col=1}}\n\t\t\t\t\t\t\t\t\t{{#if view.showInputField}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.cascadeNodeCode\" class=\"cascadeEntryCode\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._code\"}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.cascadeNodeName\" class=\"cascadeEntryName\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._item_name_mandatory\"}}\n\t\t\t\t\t\t\t\t\t\t<a {{action \"addNewNode\" target=\"this\"}}><button class=\"addCascadeItem\">{{t _add}}</button></a>\n\t\t\t\t\t\t\t\t\t{{/if}}\n\t\t\t\t\t\t\t\t{{/view}}\n                        \t</fieldset>\n                        {{/view}}\n                        <ul>\n                        \t{{#each item in FLOW.cascadeNodeControl.displayLevel1}}\n\t\t\t\t\t\t\t\t{{#view FLOW.CascadeNodeItemView contentBinding=\"item\" col=1}}\n\t\t\t\t\t\t\t\t\t{{#if view.showEditNode}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.newCode\" class=\"cascadeEntryCode squeezeR\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._code\"}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.newName\" class=\"cascadeEntryName squeezeR\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._item_name_mandatory\"}}\n\t\t\t\t\t\t\t\t\t\t<button class=\"addCascadeItem squeezeR\" {{action \"saveEditNode\" target=\"this\"}}>Save</button>\n\t\t\t\t\t\t\t\t\t\t<button class=\"addCascadeItem squeezeR\" {{action \"cancelEditNode\" target=\"this\"}}>Cancel</button>\n\t\t\t\t\t\t\t\t\t{{else}}\n\t\t\t\t\t\t\t\t\t\t<a {{action \"makeSelected\" target=\"this\"}}>{{unbound item.name}}</a>\n\t\t\t\t\t\t\t\t\t\t<a {{action \"deleteNode\" target=\"this\"}} class=\"deleteLevel\">{{t _delete}}</a>\n\t\t\t\t\t\t\t\t\t\t<a {{action \"showEditNodeField\" target=\"this\"}} class=\"editLevel\">{{t _edit}}</a>\n\t\t\t\t\t\t\t\t\t{{/if}}\n\t\t\t\t\t\t\t\t{{/view}}\n\t\t\t\t\t\t\t{{/each}}\n                        </ul>\n                    </div>\n                    <div {{bindAttr class=\"view.hideColumn2:hidden :levelCol :level02\"}}>\n                    \t{{#view FLOW.CascadeLevelNameView col=2 origLevelName=FLOW.cascadeResourceControl.displayLevelName2}}\n           \t\t            <span class=\"levelNbr\">Level {{FLOW.cascadeResourceControl.displayLevelNum2}}</span>\n                \t        <fieldset class=\"levelHead\">\n                    \t        {{#if view.editFieldVisible}}\n\t\t\t\t \t\t\t\t\t{{view Ember.TextField valueBinding=\"view.levelName\"}}\n\t          \t\t\t\t\t\t\t<a {{action \"saveNewLevelName\" target=\"this\"}} class=\"smallBtn\">{{t _save}}</a>\n\t          \t\t\t\t\t\t\t<a {{action \"cancelNewLevelName\" target=\"this\"}}>{{t _cancel}}</a>\n\t\t\t\t\t\t\t\t{{else}}\n\t\t\t\t\t\t\t\t\t<label for=\"level02Name\">{{FLOW.cascadeResourceControl.displayLevelName2}}\n\t\t\t\t\t\t\t\t\t<a {{action \"showEditField\" target=\"this\"}} class=\"editSurvey\">{{t _edit}}</a></label>\n\t\t\t\t\t\t\t\t{{/if}}\n                           \t\t{{#view FLOW.CascadeNodeView col=2}}\n\t\t\t\t\t\t\t\t\t{{#if view.showInputField}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.cascadeNodeCode\" class=\"cascadeEntryCode\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._code\"}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.cascadeNodeName\" class=\"cascadeEntryName\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._item_name_mandatory\"}}\n\t\t\t\t\t\t\t\t\t\t<a {{action \"addNewNode\" target=\"this\"}}><button class=\"addCascadeItem\">{{t _add}}</button></a>\n\t\t\t\t\t\t\t\t\t{{/if}}\n\t\t\t\t\t\t\t\t{{/view}}\n                        </fieldset>\n                       {{/view}}\n                        <ul>\n                        \t{{#each item in FLOW.cascadeNodeControl.displayLevel2}}\n\t\t\t\t\t\t\t\t{{#view FLOW.CascadeNodeItemView contentBinding=\"item\" col=2}}\n\t\t\t\t\t\t\t\t\t{{#if view.showEditNode}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.newCode\" class=\"cascadeEntryCode\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._code\"}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.newName\" class=\"cascadeEntryName\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._item_name_mandatory\"}}\n\t\t\t\t\t\t\t\t\t\t<button class=\"addCascadeItem squeezeR\" {{action \"saveEditNode\" target=\"this\"}}>{{t _save}}</button>\n\t\t\t\t\t\t\t\t\t\t<button class=\"addCascadeItem squeezeR\" {{action \"cancelEditNode\" target=\"this\"}}>{{t _cancel}}</button>\n\t\t\t\t\t\t\t\t\t{{else}}\n\t\t\t\t\t\t\t\t\t\t<a {{action \"makeSelected\" target=\"this\"}}>{{unbound item.name}}</a>\n\t\t\t\t\t\t\t\t\t\t<a {{action \"deleteNode\" target=\"this\"}} class=\"deleteLevel\">{{t _delete}}</a>\n\t\t\t\t\t\t\t\t\t\t<a {{action \"showEditNodeField\" target=\"this\"}} class=\"editLevel\">{{t _edit}}</a>\n\t\t\t\t\t\t\t\t\t{{/if}}\n\t\t\t\t\t\t\t\t{{/view}}\n\t\t\t\t\t\t\t{{/each}}\n                        </ul>\n                    </div>\n                    <div {{bindAttr class=\"view.hideColumn3:hidden :levelCol :level03\"}}>\n                    {{#view FLOW.CascadeLevelNameView col=3 origLevelName=FLOW.cascadeResourceControl.displayLevelName3}}\n                        <span class=\"levelNbr\">Level {{FLOW.cascadeResourceControl.displayLevelNum3}}</span>\n                        <fieldset class=\"levelHead\">\n                             {{#if view.editFieldVisible}}\n\t\t\t\t \t\t\t\t{{view Ember.TextField valueBinding=\"view.levelName\"}}\n\t          \t\t\t\t\t\t<a {{action \"saveNewLevelName\" target=\"this\"}} class=\"smallBtn\">{{t _save}}</a>\n\t          \t\t\t\t\t\t<a {{action \"cancelNewLevelName\" target=\"this\"}}>{{t _cancel}}</a>\n\t\t\t\t\t\t\t{{else}}\n\t\t\t\t\t\t\t\t<label for=\"level02Name\">{{FLOW.cascadeResourceControl.displayLevelName3}}\n\t\t\t\t\t\t\t\t<a {{action \"showEditField\" target=\"this\"}} class=\"editSurvey\">{{t _edit}}</a></label>\n\t\t\t\t\t\t\t{{/if}}\n\t\t\t\t\t\t\t{{#view FLOW.CascadeNodeView col=3}}\n\t\t\t\t\t\t\t\t{{#if view.showInputField}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.cascadeNodeCode\" class=\"cascadeEntryCode\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._code\"}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.cascadeNodeName\" class=\"cascadeEntryName\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._item_name_mandatory\"}}\n\t\t\t\t\t\t\t\t\t\t<a {{action \"addNewNode\" target=\"this\"}}><button class=\"addCascadeItem\">{{t _add}}</button></a>\n\t\t\t\t\t\t\t\t{{/if}}\n\t\t\t\t\t\t\t{{/view}}\n                        </fieldset>\n                        {{/view}}\n                        <ul>\n                        \t{{#each item in FLOW.cascadeNodeControl.displayLevel3}}\n\t\t\t\t\t\t\t\t{{#view FLOW.CascadeNodeItemView contentBinding=\"item\" col=3}}\n\t\t\t\t\t\t\t\t\t{{#if view.showEditNode}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.newCode\" class=\"cascadeEntryCode\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._code\"}}\n\t\t\t\t\t\t\t\t\t\t{{view Ember.TextField valueBinding=\"view.newName\" class=\"cascadeEntryName\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._item_name_mandatory\"}}\n\t\t\t\t\t\t\t\t\t\t<button class=\"addCascadeItem squeezeR\" {{action \"saveEditNode\" target=\"this\"}}>Save</button>\n\t\t\t\t\t\t\t\t\t\t<button class=\"addCascadeItem squeezeR\" {{action \"cancelEditNode\" target=\"this\"}}>Cancel</button>\n\t\t\t\t\t\t\t\t\t{{else}}\n\t\t\t\t\t\t\t\t\t\t<a {{action \"makeSelected\" target=\"this\"}}>{{unbound item.name}}</a>\n\t\t\t\t\t\t\t\t\t\t<a {{action \"deleteNode\" target=\"this\"}} class=\"deleteLevel\">{{t _delete}}</a>\n\t\t\t\t\t\t\t\t\t\t<a {{action \"showEditNodeField\" target=\"this\"}} class=\"editLevel\">{{t _edit}}</a>\n\t\t\t\t\t\t\t\t\t{{/if}}\n\t\t\t\t\t\t\t\t{{/view}}\n\t\t\t\t\t\t\t{{/each}}\n                        </ul>\n                    </div>\n                </section>\n            </section>\n            <section {{bindAttr class=\"view.showImportDialog:hidden:shown  :levelSecondNav\"}}>\n\t            <nav>\n\t              <ul>\n\t               {{#view FLOW.CascadeSecondNavView dir=\"down\"}}\n\t               \t\t<a {{action \"goDownLevel\" target=\"this\"}} class=\"levelDown btn\">{{t _move_left}}</a>\n\t              {{/view}}\n\t               {{#view FLOW.CascadeSecondNavView dir=\"up\"}}\n\t                \t<a {{action \"goUpLevel\" target=\"this\"}} class=\"levelUp btn\">{{t _move_right}}</a>\n\t              {{/view}}\n\t              </ul>\n\t            </nav>\n          </section>\n            {{else}}\n            <section class=\"noCascadeContainer\">\n                    <ul>\n                        <li class=\"formList\">\n\t\t\t\t\t\t\t<p class=\"noCascade\">{{t _select_or_create_cascade}}</p>\n                        </li>\n                    </ul>\n                </section>\n          {{/if}}\n\n        </section>\n    </div>\n</section>\n{{/view}}\n");

});

loader.register('akvo-flow/templates/navData/data-approval-group-list', function(require) {

return Ember.Handlebars.compile("<section class=\"fullWidth\" id=\"dataApproval\" >\n    <nav class=\"menuTopbar\">\n        <ul>\n            <li><a {{action doAddApprovalGroup}} class=\"standardBtn\">{{t _add_approval_group}}</a></li>\n        </ul>\n    </nav>\n    <table class=\"dataTable\" id=\"dataApprovalTable\">\n        <thead>\n            <tr>\n                <th class=\"noArrows\">{{t _name}}</th>\n                <th class=\"noArrows\">{{t _type}}</th>\n                <th class=\"noArrows\"></th>\n            </tr>\n        </thead>\n        <tbody>\n            {{#each item in controller}}\n            <tr>\n                <td class=\"name\">{{item.name}}</td>\n                <td class=\"approval-type\">{{#if item.ordered}}{{t _ordered}}{{else}}{{t _unordered}}{{/if}}</td>\n                <td class=\"action\">\n                    <ul>\n                        <li class=\"editIconSmall\"><a {{action doEditApprovalGroup item}}>{{t _edit}}</a></li>\n                        <li class=\"deleteIconSmall\"><a {{action doDeleteApprovalGroup item}}>{{t _delete}}</a></li>\n                    </ul>\n                </td>\n            </tr>\n            {{/each}}\n        </tbody>\n        <tfoot>\n            <tr>\n                <td colspan=\"7\"><small></small>\n                </td>\n            </tr>\n        </tfoot>\n    </table>\n</section>");

});

loader.register('akvo-flow/templates/navData/data-approval-group', function(require) {

return Ember.Handlebars.compile("<section class=\"fullWidth\" id=\"dataApproval\">\n    <div class=\"addWorkflow\">\n        {{#if keyId}}\n        <h3>{{t _edit_approval_group}}</h3>\n        {{else}}\n        <h3>{{t _add_approval_group}}</h3>\n        {{/if}}\n        <form>\n                <label>{{t _name}}: &nbsp;{{view Ember.TextField valueBinding=\"name\" size=30}}</label>\n                <label>{{t _approval_type}}: {{view Ember.Select contentBinding=\"view.approvalTypeOptions\"\n                                                                    optionLabelPath=\"content.label\"\n                                                                    optionValuePath=\"content.optionValue\"\n                                                                    valueBinding=\"isOrderedApprovalGroup\"}}\n                </label>\n                <label>{{t _steps}}: {{outlet approvalStepsOutlet}}</label>\n                <div class=\"confirmWorkflow\">\n                     <a {{action \"doCancelEditApprovalGroup\"}}>{{t _cancel}}</a> &nbsp;&nbsp; <a {{action \"doSaveApprovalGroup\"}} class=\"smallBtn\">{{t _save}}</a>\n                </div>\n        </form>\n    </div>\n</section>");

});

loader.register('akvo-flow/templates/navData/data-approval-steps', function(require) {

return Ember.Handlebars.compile("<ul id=\"approvalSteps\">\n\t{{#each step in controller}}\n\t    <div class=\"eachStep\">\n\t\t    <li><h4>{{view Ember.TextField valueBinding=\"step.title\" size=80}}</h4></li>\n\t\t    <a {{action deleteApprovalStep step target=\"controller\"}}>{{t _delete}}</a>\n\t    </div>\n\t{{/each}}\n\t<a {{action addApprovalStep target=\"controller\"}} class=\"addFolder addStep\">{{t _add_step}}</a>\n</ul>\n");

});

loader.register('akvo-flow/templates/navData/data-cleaning', function(require) {

return Ember.Handlebars.compile("<section class=\"fullWidth reportTools\" id=\"reportBlocks\">\n  {{#view FLOW.ExportReportsAppletView}}\n\n    {{#unless FLOW.projectControl.isLoading}}\n      {{view FLOW.DataCleaningSurveySelectionView}}\n    {{/unless}}\n\n    {{#if FLOW.selectedControl.selectedSurveyGroup}}\n      {{view Ember.Select\n          contentBinding=\"FLOW.surveyControl.arrangedContent\"\n          selectionBinding=\"FLOW.selectedControl.selectedSurvey\"\n          optionLabelPath=\"content.code\"\n          optionValuePath=\"content.keyId\"\n          prompt=\"\"\n          promptBinding=\"Ember.STRINGS._select_form\"}}\n    {{/if}}\n\n    <div class=\"rawDataReport block\">\n      <h3>{{t _data_cleaning_export}}</h3>\n      <p>{{t _raw_data_report_applet_text_short}}</p>\n      <div class=\"dataCollectedDate\">\n          <label class=\"collectedFrom\">\n            <span>{{t _collected_from}}:</span> {{view FLOW.DateField minDate=false valueBinding=\"FLOW.dateControl.fromDate\" elementId=\"from_date\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._collected_from\" size=30}}\n          </label>\n          <label class=\"collectedTo\">\n            <span>{{t _to}}:</span> {{view FLOW.DateField minDate=false valueBinding=\"FLOW.dateControl.toDate\" elementId=\"to_date\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._to\" size=30}}\n          </label>\n      </div>\n      <a {{action showDataCleaningReport target=\"this\"}} class=\"standardBtn\"> {{t _download}} </a>\n    </div>\n\n    <div class=\"rawDataReport block\">\n      <h3>{{t _import_data}}</h3>\n      <p>{{t _import_raw_data_applet_text_short_}}</p>\n      {{#if FLOW.uploader.support}}\n        <p><input id=\"raw-data-import-file\" type=\"file\"/></p>\n        <a {{action importFile target=\"this\"}} class=\"standardBtn\"> {{t _import}}</a>\n        <div class=\"resumable-progress\">\n          <h5>{{t _progress}}</h5>\n          <div class=\"progress-container\"><div class=\"progress-bar\"></div></div>\n        </div>\n        <ul class=\"resumable-list\"></ul>\n      {{else}}\n       <div class=\"resumable-error\">\n        {{t _bulk_upload_unsupported_browser}}\n       </div>\n      {{/if}}\n    </div>\n  {{/view}}\n</section>\n");

});

loader.register('akvo-flow/templates/navData/data-subnav', function(require) {

return Ember.Handlebars.compile("<ul>\n    {{#view view.NavItemView item=\"inspectData\" }}\n    <a {{action doInspectData}}>{{t _inspect_data}}</a>\n    {{/view}}\n    {{#if FLOW.Env.showMonitoringFeature}}\n      {{#view view.NavItemView item=\"monitoringData\" }}\n        <a {{action doMonitoringData}}>{{t _monitoring}}</a>\n      {{/view}}\n    {{/if}}\n    {{#view view.NavItemView item=\"bulkUpload\" }}\n    <a {{action doBulkUpload}}>{{t _bulk_upload_data}}</a>\n    {{/view}} \n    {{#view view.NavItemView item=\"dataCleaning\" }}\n        {{#if view.showDataCleaningButton}}\n        <a {{action doDataCleaning}}>{{t _data_cleaning}}</a>\n        {{/if}}\n    {{/view}}\n    {{#view view.NavItemView item=\"cascadeResources\" }}\n        {{#if view.showCascadeResourcesButton}}\n        <a {{action doCascadeResources}}>{{t _cascade_resources}}</a>\n        {{/if}}\n    {{/view}}\n    {{#view view.NavItemView item=\"approvalGroup\" }}\n        {{#if view.showDataApprovalButton}}\n        <a {{action doDataApproval}}>{{t _data_approval}}</a>\n        {{/if}}\n    {{/view}}\n</ul> \n  ");

});

loader.register('akvo-flow/templates/navData/inspect-data', function(require) {

return Ember.Handlebars.compile("{{#view FLOW.inspectDataTableView}}\n<section class=\"\" id=\"inspectData\">\n    <div class=\"floats-in filterData\" id=\"dataFilter\">\n        <div class=\"chooseSurveyData\">\n          {{#unless FLOW.projectControl.isLoading}}\n            {{view FLOW.SurveySelectionView}}\n          {{/unless}}\n          {{#if FLOW.selectedControl.selectedSurveyGroup}}\n            {{view Ember.Select contentBinding=\"FLOW.surveyControl.arrangedContent\" selectionBinding=\"FLOW.selectedControl.selectedSurvey\" optionLabelPath=\"content.code\" optionValuePath=\"content.keyId\" prompt=\"\" promptBinding=\"Ember.STRINGS._select_form\"}}\n          {{/if}}\n        </div>\n        <div class=\"dataDeviceId\">\n            {{#if view.validSurveyInstanceId}}\n              <label class=\"surveyInstanceId\">{{t _instance_id}}:</label>\n            {{else}}\n              <label class=\"surveyInstanceId tooltip\"><a class=\"tooltip\" title=\"{{t _instance_id_must_be_a_number}}\">{{t _instance_id}}</a>:</label>\n            {{/if}}\n            {{view Ember.TextField valueBinding=\"view.surveyInstanceId\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._instance_id\" size=30}}\n            <label class=\"devideId\">{{t _device_id}}:</label>\n            {{view Ember.TextField valueBinding=\"view.deviceId\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._device_id\" size=30}}\n            <label class=\"submitterName\">{{t _submitter_name}}:</label>\n            {{view Ember.TextField valueBinding=\"view.submitterName\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._submitter_name\" size=30}}\n            <label class=\"collectedFrom\"><span>{{t _collected_from}}:</span> {{view FLOW.DateField minDate=false valueBinding=\"FLOW.dateControl.fromDate\" elementId=\"from_date\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._collected_from\" size=30}}\n            </label>\n\n            <label class=\"collectedTo\"><span>{{t _to}}:</span> {{view FLOW.DateField minDate=false valueBinding=\"FLOW.dateControl.toDate\" elementId=\"to_date\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._to\" size=30}}\n            </label>\n        </div>\n\n        <div class=\"chooseLocationData\">\n            <a {{action \"doFindSurveyInstances\" target=\"this\" }} class=\"findData standardBtn btnAboveTable\"> {{t _find}}</a>\n        </div>\n    </div>\n    {{#if view.noResults}}\n      <div class=\"noResults\">{{t _no_results_found}}</div>\n    {{/if}}\n\n    <section class=\"fullWidth \" id=\"devicesList\">\n        <table class=\"dataTable\" id=\"surveyDataTable\">\n            <thead>\n                <tr>\n                    <th class=\"noArrows\"></th>\n                    <th class=\"noArrows\">{{t _instance_id}}</th>\n                    <th class=\"noArrows\">{{t _submitter}}</th>\n                    <th class=\"noArrows\">{{t _device}}</th>\n                    <th class=\"noArrows\">{{t _collected}}</th>\n                    <th class=\"noArrows\">{{t _action}}</th>\n                </tr>\n            </thead>\n            <tbody>\n                {{#each SI in FLOW.surveyInstanceControl.currentContents}}\n                <tr>\n                    <td class=\"device\">{{#view FLOW.DataNumView contentBinding=\"this\" pageNumberBinding=\"FLOW.surveyInstanceControl.pageNumber\"}}{{view.rownum}}{{/view}}</td>\n                    <td class=\"device\">{{SI.keyId}}</td>\n                    <td class=\"submitter\">{{SI.submitterName}}</td>\n                    <td class=\"device\">{{SI.deviceIdentifier}}</td>\n                    <td class=\"collected\">{{#with SI}}{{date1 collectionDate}}{{/with}}</td>\n                    <td class=\"action\">\n                        <a {{action showEditSurveyInstanceWindow SI target=\"this\" }}>\n                            {{#if view.showEditResponseLink}}\n                                {{t _edit}}\n                            {{else}}\n                                {{t _view}}\n                            {{/if}}\n                        </a>\n                        {{#if view.showSurveyInstanceDeleteButton }}\n                            {{#view FLOW.DataItemView contentBinding=\"SI\"}}\n                                <a {{action confirm FLOW.dialogControl.delSI2 target=\"FLOW.dialogControl\" }}>\n                                    {{t _delete}}\n                                </a>\n                            {{/view}}\n                        {{/if}}\n                    </td>\n                </tr>\n                {{/each}}\n            </tbody>\n        </table>\n        <ul class=\"prevNext\">\n            <li class=\"prevBtn\">{{#if view.hasPrevPage}}\n                <a {{action doPrevPage target='this' }}> &lsaquo; {{t _previous}}</a> {{/if}}\n            </li>\n            <li class=\"nextBtn\">{{#if view.hasNextPage}}\n                <a {{action doNextPage target='this' }}>{{t _next}} &rsaquo;</a>{{/if}}\n            </li>\n        </ul>\n    </section>\n\n    <!-- edit surveyInstance popup-->\n    <div {{bindAttr class=\"view.showEditSurveyInstanceWindowBool:display :overlay\" }}>\n        <div class=\"blanketWide\"></div>\n        <div class=\"dialogWrap\">\n            <!-- the dialog contents -->\n            <div class=\"confirmDialog dialogWide\">\n                <a {{action \"doCloseEditSIWindow\" target=\"this\" }} class=\"ok closeDialog\">{{t _close_window}}</a>\n                {{#if view.showEditResponseLink}}\n                <h2>{{t _edit_answers}}</h2>\n                {{else}}\n                <h2>{{t _view_answers}}</h2>\n                {{/if}}\n                <nav class=\"editAnswerMenu\">\n                    <ul>\n                        <li class=\"prevBtn\"><a {{action \"doPreviousSI\" target=\"this\" }}>&lsaquo; {{t _previous_record}}</a>\n                        </li>\n                        {{#if view.showSurveyInstanceDeleteButton }}\n                        <li class=\"deleteBtn\"><a {{action confirm FLOW.dialogControl.delSI target=\"FLOW.dialogControl\" }} class=\"smallBtn\">{{t _delete}}</a>\n                        </li>\n                        {{/if}}\n                        <!--  <li class=\"saveBtn\"><a {{action \"doSaveSI\" target=\"this\"}} class=\"smallBtn\">{{t _save}}</a></li> -->\n                        <li class=\"nextBtn\"><a {{action \"doNextSI\" target=\"this\" }}>{{t _next_record}} &rsaquo;</a>\n                        </li>\n                    </ul>\n                </nav>\n                <p>{{view.siString}}</p>\n\n                <table class=\"dataTable\" id=\"surveyDataTable\">\n                    <!-- TABLE HEADER-->\n                    <thead>\n                        <tr>\n                            <th class=\"noArrows\" style=\"width:10%\"></th>\n                            <th class=\"noArrows\" style=\"width:40%\">{{t _question}}</th>\n                            <th class=\"noArrows\" style=\"width:50%\">{{t _answer}}</th>\n                        </tr>\n                    </thead>\n                    <!-- TABLE BODY: MAIN CONTENT-->\n                    <tbody>\n                  {{#each responseSubList in FLOW.questionAnswerControl.contentByGroup}}\n                    {{#each iterations in responseSubList}}\n                      <tr><td colspan=\"3\" style=\"text-align:left\"><h3 class=\"questionGroupName\">{{iterations.groupName}}</h3></td></tr>\n                            {{each iterations itemViewClass=\"FLOW.QuestionAnswerInspectDataView\"}}\n                    {{/each}}\n                  {{/each}}\n                    </tbody>\n                  </table>\n            </div>\n        </div>\n    </div>\n</section>\n{{/view}}\n");

});

loader.register('akvo-flow/templates/navData/monitoring-data-row', function(require) {

return Ember.Handlebars.compile("<tr>\n    <td class=\"device\">{{view.dataPointRowNumber}}</td>\n    <td class=\"displayName\" style=\"text-align:left;\">{{displayName}}</td>\n    <td class=\"identifier\" style=\"text-align:left;\">{{identifier}}</td>\n    <td class=\"collected\">{{date1 lastUpdateDateTime}}</td>\n\n    {{#if view.parentView.showApprovalStatusColumn}}\n    <td class=\"approvalStatus\">\n        <a {{action toggleShowDataApprovalBlock target=\"view\"}}>\n            {{#view FLOW.DataPointApprovalStatusView}}{{view.dataPointApprovalStatus}}{{/view}}\n        </a>\n    </td>\n    {{/if}}\n\n    <td class=\"action\">\n        <a {{action showDetailsDialog this target=\"view.parentView\" }}>{{t _view_details}}</a>\n    </td>\n</tr>\n\n{{#if view.showDataApprovalBlock}}\n<tr class=\"dataApprovalRow\">\n    <td></td>\n    <td class=\"dataApprovalBlock\"  colspan=\"5\">\n        <ol class=\"dataPointApproval\">\n        {{#each step in FLOW.router.approvalStepsController}}\n            <li>\n            <h4>{{step.title}}<h4>\n            {{#view FLOW.DataPointApprovalView stepBinding=\"step\"\n                                                dataPointBinding=\"view.content\"\n                                                target=\"view\"}}\n                {{#if view.isApprovedStep}}\n                    {{#with view.dataPointApproval}}\n                        <div class=\"approvalStatus\">{{status}}</div>\n                        <div class=\"approvalDate\">{{date3 approvalDate}} | {{approverUserName}}</div>\n                        <div class=\"approvalComment\">{{comment}}</div>\n                    {{/with}}\n                {{/if}}\n                {{#if view.showApprovalFields}}\n                    {{view Ember.Select contentBinding=\"view.parentView.approvalStatus\"\n                                        optionLabelPath=\"content.label\"\n                                        optionValuePath=\"content.value\"\n                                        valueBinding=\"view.dataPointApproval.status\"}}\n                    {{view Ember.TextField size=\"80\" valueBinding=\"view.dataPointApproval.comment\"}}\n                    <a class=\"btn\" {{action submitDataPointApproval target=\"view\"}}>{{t _submit}}</a>\n                {{/if}}\n            {{/view}}\n            </li>\n        {{/each}}\n        </ol>\n    </td>\n</tr>\n{{/if}}\n");

});

loader.register('akvo-flow/templates/navData/monitoring-data', function(require) {

return Ember.Handlebars.compile("{{#view FLOW.MonitoringDataTableView}}\n<section class=\"\" id=\"monitoringData\">\n    <div class=\"floats-in filterData\" id=\"dataFilter\">\n      <div class=\"chooseSurveyData\">\n      {{#unless FLOW.projectControl.isLoading}}\n        {{view FLOW.SurveySelectionView showMonitoringSurveysOnly=true}}\n      {{/unless}}\n    </div>\n    <div class=\"dataDeviceId\">\n    <label  class=\"devideId\"> {{t _identifier}}:</label>\n      {{view Ember.TextField\n          valueBinding=\"view.identifier\"\n          placeholder=\"\"\n          placeholderBinding=\"Ember.STRINGS._identifier\"\n          size=30}}\n        <label  class=\"devideId\"> {{t _data_point_name}}:</label>\n        {{view Ember.TextField\n          valueBinding=\"view.displayName\"\n          placeholder=\"\"\n          placeholderBinding=\"Ember.STRINGS._data_point_name\"\n          size=30}}\n      </div>\n      <a {{action \"findSurveyedLocale\" target=\"this\"}} class=\"findData standardBtn btnAboveTable\">{{t _find}}</a>\n  </div>\n    {{#if view.noResults}}\n      <div class=\"noResults\">{{t _no_results_found}}</div>\n    {{/if}}\n    \n    <section class=\"fullWidth \" id=\"devicesList\">\n      <table class=\"dataTable\" id=\"surveyDataTable\">\n        <thead>\n          <tr>\n              <th class=\"noArrows\"></th>\n              <th class=\"noArrows\">{{t _data_point_name}}</th>\n              <th class=\"noArrows\">{{t _identifier}}</th>\n              <th class=\"noArrows\">{{t _last_update}}</th>\n              {{#if view.showApprovalStatusColumn}}\n              <th class=\"noArrows\">{{t _approval_status}}</th>\n              {{/if}}\n              <th class=\"noArrows\">{{t _action}}</th>\n          </tr>\n        </thead>\n        <tbody>\n          {{#each FLOW.router.surveyedLocaleController.currentContents}}\n           {{view FLOW.DataPointView contentBinding=\"this\"}}\n          {{/each}}\n        </tbody>\n      </table>\n      <ul class=\"prevNext\">\n        <li class=\"prevBtn\">{{#if view.hasPrevPage}}\n          <a {{action doPrevPage target='this'}}> &lsaquo; {{t _previous}}</a> {{/if}}\n        </li>\n        <li class=\"nextBtn\">{{#if view.hasNextPage}}\n          <a {{action doNextPage target='this'}}>{{t _next}} &rsaquo;</a>{{/if}}\n        </li>\n      </ul>\n    </section>\n\n    <!-- surveyInstance details Dialog -->\n    <div {{bindAttr class=\"view.showingDetailsDialog:display :overlay\"}}>\n      <div class=\"blanketWide\"></div>\n          <div class=\"dialogWrap\">\n        <!-- the dialog contents -->\n        <div class=\"confirmDialog dialogWide\">\n         <a {{action \"closeDetailsDialog\" target=\"this\"}} class=\"ok closeDialog\">{{t _close_window}}</a>\n\n          <h2>{{t _details}}</h2>\n            <nav class=\"editAnswerMenu\">\n          </nav>\n        <p></p>\n      <table class=\"dataTable\" id=\"surveyDataTable\">\n        <thead>\n          <tr>\n              <th class=\"noArrows\"></th>\n              <th class=\"noArrows\">{{t _id}}</th>\n              <th class=\"noArrows\">{{t _survey}}</th>\n              <th class=\"noArrows\">{{t _submitter}}</th>\n              <th class=\"noArrows\">{{t _device}}</th>\n              <th class=\"noArrows\">{{t _collected}}</th>\n              <th class=\"noArrows\">{{t _action}}</th>\n          </tr>\n        </thead>\n        <tbody>\n          {{#each SI in FLOW.surveyInstanceControl.currentContents}}\n            <tr>\n              <td class=\"device\">{{#view FLOW.DataNumView contentBinding=\"this\"}}{{view.rownum}}{{/view}}</td>\n              <td class=\"device\">{{SI.keyId}}</td>\n              <td class=\"survey\" style=\"text-align:left;\">{{SI.surveyCode}}</td>\n              <td class=\"submitter\" style=\"text-align:left;\">{{SI.submitterName}}</td>\n              <td class=\"device\">{{SI.deviceIdentifier}}</td>\n              <td class=\"collected\">{{#with SI}}{{date1 collectionDate}}{{/with}}</td>\n              <td class=\"action\">\n                <a {{action showSurveyInstanceDetails SI target=\"this\"}}>\n                  {{t _view_details}}\n                </a>\n              </td>\n            </tr>\n            <tr class=\"si_details\" style=\"background: #fff; display:none;\" data-flow-id=\"si_details_{{unbound SI.id}}\">\n              <td colspan=\"7\">\n              <table class=\"dataTable\" id=\"surveyDataTable\">\n            <!-- TABLE HEADER-->\n            <thead>\n                <tr>\n                    <th class=\"noArrows\" style=\"width:10%\"></th>\n                    <th class=\"noArrows\">{{t _question}}</th>\n                    <th class=\"noArrows\">{{t _answer}}</th>\n                </tr>\n            </thead>\n            <!-- TABLE BODY: MAIN CONTENT-->\n            <tbody>\n                {{#each responseSublist in FLOW.questionAnswerControl.contentByGroup}}\n                   {{#each iterations in responseSublist}}\n                       <tr><td colspan=\"3\" style=\"text-align:left\"><h3 class=\"questionGroupName\">{{iterations.groupName}}</h3></td></tr>\n                            {{each iterations itemViewClass = \"FLOW.QuestionAnswerMonitorDataView\"}}\n                   {{/each}}\n                {{/each}}\n            </tbody>\n            <!-- TABLE FOOTER-->\n          </table>\n              </td>\n            </tr>\n          {{/each}}\n        </tbody>\n      </table>\n     </div>\n    </div>\n  </div>\n</section>\n{{/view}}\n");

});

loader.register('akvo-flow/templates/navData/nav-data', function(require) {

return Ember.Handlebars.compile("<section class=\"topBar\">\n    <div id=\"tabs\">\n        <nav class=\"tabNav floats-in\">\n  \t\t\t{{view FLOW.DatasubnavView controllerBinding=\"controller.controllers.datasubnavController\"}}\n        </nav>    \n    </div>\n</section>    \n<section class=\"dataSection floats-in belowHeader\" id=\"main\" role=\"main\">\n    {{outlet}}\n</section>\n");

});

loader.register('akvo-flow/templates/navData/question-answer', function(require) {

return Ember.Handlebars.compile("<tr>\n    <td class=\"device\" style=\"width:10%\">{{view.question.order}}</td>\n    <td class=\"survey\" style=\"text-align:left;width:40%\">{{view.question.text}}</td>\n    <td {{bindAttr class=\":submitter view.isMultipleSelectOption:multiple-option\"}} style=\"text-align:left\">\n        {{#if view.inEditMode}}\n            {{#if view.isOptionType}}\n                    {{#if view.isMultipleSelectOption}}\n                        {{view FLOW.QuestionAnswerMultiOptionEditView contentBinding=\"view.optionsList\" selectionBinding=\"view.multiSelectOptionValue\"}}\n                    {{else}}\n                        {{view Ember.Select\n                            contentBinding=\"view.optionsList\"\n                            optionLabelPath=\"content.text\"\n                            optionValuePath=\"content.code\"\n                            selectionBinding=\"view.singleSelectOptionValue\"}}\n                    {{/if}}\n                    {{#if view.isOtherOptionSelected}}\n                        <div class=\"editOtherContainer\">\n                            <span class=\"otherLabel\">{{t _other}}</span>{{view Ember.TextField class=\"editOtherText\" size=25 valueBinding=\"view.optionValue.lastObject.otherText\"}}\n                        </div>\n                    {{/if}}\n            {{else}} {{#if view.isNumberType}}\n                    {{view Ember.TextField valueBinding=\"view.numberValue\" size=10 }}\n            {{else}} {{#if view.isTextType}}\n                        {{view Ember.TextField valueBinding=\"value\" size=10 }}\n            {{else}} {{#if view.isCascadeType}}\n                        {{view Ember.TextField valueBinding=\"view.cascadeValue\" size=20 }}\n            {{else}} {{#if view.isDateType}}\n                            {{view FLOW.DateField2 valueBinding=\"view.date\" size=20}}\n            {{else}} {{#if view.isBarcodeType}}\n                                {{t _the_barcode_app_on_the_device_is_used_here}}\n                                {{view Ember.TextField valueBinding=\"value\" size=10 }}\n            {{/if}} {{/if}} {{/if}} {{/if}} {{/if}} {{/if}}\n        <a {{action doSave target=\"this\" }} class=\"smallBtn\">{{t _save}}</a>  <a {{action doCancel target=\"this\" }}>{{t _cancel}}</a>\n        {{else}}\n            {{#if view.isNotEditable}}\n                {{#if view.isPhotoType}}\n                    {{view.photoUrl}} <a {{bindAttr href=\"view.photoUrl\"}} target=\"_blank\">{{t _open_photo}}</a>\n                    {{#if view.photoLocation}}\n                        <br>{{view.photoLocation}}\n                    {{/if}}\n                {{else}} {{#if view.isVideoType}}\n                    {{view.photoUrl}} <a {{bindAttr href=\"view.photoUrl\"}} target=\"_blank\">{{t _open_video}}</a>\n                {{else}} {{#if view.isDateType}}\n                    {{date3 value}}\n                {{else}} {{#if view.isCascadeType}}\n                    {{view.cascadeValue}}\n                {{else}} {{#if view.isOptionType}}\n                    {{view FLOW.QuestionAnswerOptionListView contentBinding=\"view.optionValue\"}}\n                {{else}} {{#if view.isGeoShapeType}}\n                  {{view FLOW.GeoshapeMapView}}\n                {{else}} {{#if view.isSignatureType}}\n                    {{#if view.signatureImageSrc}}\n                        <div class=\"signatureImage\"><img {{bindAttr src=\"view.signatureImageSrc\"}} /></div>\n                        <div class=\"signedBySection\">{{t _signed_by}}: {{view.signatureSignatory}}</div>\n                    {{else}}\n                        {{t _no_signature_found}}\n                    {{/if}}\n                {{else}} {{#if view.isCaddisflyType}}\n                    <div class=\"\"><strong>{{view.testName}}</strong></div>\n                    {{#each result in view.testResult}}\n                      <br><div>{{result.name}} : {{result.value}} {{result.unit}}</div>\n                    {{/each}}<br>\n                    <div class=\"signatureImage\"><img {{bindAttr src=\"view.caddisflyImageURL\"}} /></div>\n                {{else}}\n                    {{value}}\n                {{/if}} {{/if}} {{/if}} {{/if}} {{/if}} {{/if}} {{/if}}{{/if}}\n            {{else}}\n                {{#if view.isDateType}}\n                    <a {{action doEdit target=\"this\" }}>{{date3 value}}</a>\n                {{else}} {{#if view.isCascadeType}}\n                    <a {{action doEdit target=\"this\" }}>{{view.cascadeValue}}</a>\n                {{else}} {{#if view.isOptionType}}\n                    <a {{action doEdit target=\"this\" }}>{{view FLOW.QuestionAnswerOptionListView contentBinding=\"view.optionValue\"}}</a>\n                {{else}}\n                    <a {{action doEdit target=\"this\" }}>{{value}}{{if_blank value}}</a>\n                {{/if}} {{/if}} {{/if}}\n            {{/if}}\n        {{/if}}\n    </td>\n</tr>\n");

});

loader.register('akvo-flow/templates/navDevices/assignment-edit-tab/assignment-edit', function(require) {

return Ember.Handlebars.compile("{{#view FLOW.AssignmentEditView}}\n<section class=\"fullWidth assignmentsEdit\" id=\"assignSurveys\">\n     <a {{action \"cancelEditSurveyAssignment\" target=\"this\"}} class=\"stepBack\" id=\"float-right\">{{t _go_back_to_assignment_list}}</a>\n    <form>\n      <fieldset id=\"assignmentDetails\">\n        <h2>01. {{t _assignment_details}}</h2>\n        <label for=\"assignmentName\">{{t _assignment_name}}:</label>\n          {{view Ember.TextField\n            valueBinding=\"view.assignmentName\"\n            id=\"assignmentName\"\n            placeholder=\"\"\n            placeholderBinding=\"Ember.STRINGS._enter_a_name_for_this_assignment\"\n            size=30}}\n        <div class=\"dateRange\">\n          <div class=\"activeDate\">\n            <label for=\"startDate\">{{t _start_date}}:</label>\n           {{view FLOW.DateField valueBinding=\"FLOW.dateControl.fromDate\" elementId=\"from_date\"  size=30 class=datePicker}}\n          </div>\n          <div class=\"expireDate\">\n            <label for=\"expireDate\">{{t _expiration_date}}:</label>\n           {{view FLOW.DateField valueBinding=\"FLOW.dateControl.toDate\" elementId=\"to_date\" size=30 class=datePicker}}\n          </div>\n        </div>\n      </fieldset>\n      <div class=\"fieldSetWrap floats-in\">\n        <div class=\"formLeftPanel\">\n          <fieldset id=\"surveySelect\" class=\"floats-in\">\n            <h2>02. {{t _select_survey}}:</h2>\n            <p class=\"infoText\">{{t _cant_find_your_survey_}}</p>\n            <div class=\"SelectLayout\">\n              <label for=\"surveyGroup\">{{t _select_survey}}:</label>\n              {{#unless FLOW.projectControl.isLoading}}\n                {{view FLOW.SurveySelectionView}}\n              {{/unless}}\n  \t\t\t    </div>\n            <div class=\"formSelectorList\">\n                  <nav>\n                    <ul>\n                      <li><a {{action selectAllSurveys target=\"this\"}}>{{t _select_all}}</a></li>\n                      <li><a {{action deselectAllSurveys target=\"this\"}}>{{t _deselect_all}}</a></li>\n                    </ul>\n                  </nav>\n                  <label for=\"surveys\">{{t _select_forms}}:</label>\n                 {{view Ember.Select\n                 multiple=true\n                 size=10\n              contentBinding=\"FLOW.surveyControl.publishedContent.arrangedContent\"\n              selectionBinding=\"FLOW.selectedControl.selectedSurveys\"\n              optionLabelPath=\"content.name\"\n                id=\"surveys\"\n              optionValuePath=\"content.keyId\"}}\n            <a {{action addSelectedSurveys target=\"this\"}}  class=\"AddBtn\">{{t _add_selected_forms}}</a>\n            </div>\n          </fieldset>\n        </div>\n        <div class=\"formRightPanel\">\n          <fieldset id=\"surveyPreview\" class=\"floats-in\">\n            <h2>{{t _preview_survey_selection}}:</h2>\n            <div class=\"\">\n              <!-- DEVICES TABLE-->\n              <table id=\"surveyPreviewList\" class=\"previewList\" >\n                <!-- TABLE HEADER-->\n                <thead>\n                  <tr>\n                    <th class=\"groupPreview\">{{t _survey}}</th>\n                    <th class=\"surveyPreview\">{{t _form}}</th>\n                    <th class=\"action\"></th>\n                  </tr>\n                </thead>\n                <!-- TABLE BODY: MAIN CONTENT-->\n                <tbody>\n                  {{#each survey in view.surveysPreview}}\n                  <tr>\n                    <td class=\"groupPreview\">{{survey.surveyGroupName}}</td>\n                    <td class=\"surveyPreview\"{{survey.name}}</td>\n                    <td class=\"action\"><a {{action \"removeSingleSurvey\" survey target=\"this\"}} class=\"remove\">{{t _remove}}</a></td>\n                  </tr>\n                  {{/each}}\n                </tbody>\n                <!-- TABLE FOOTER-->\n                <tfoot>\n                  <tr>\n                    <td colspan=\"7\"><a {{action \"removeAllSurveys\" target=\"this\"}}>{{t _clear_all}}</a></td>\n                  </tr>\n                </tfoot>\n              </table>\n            </div>\n          </fieldset>\n        </div>\n      </div>\n\n    <div class=\"fieldSetWrap makeWhite\">\n        <div class=\"formLeftPanel\">\n          <fieldset id=\"devicesSelect\" class=\"floats-in\">\n            <h2>03. {{t _select_devices}}:</h2>\n            <div class=\"\">\n            <label for=\"deviceGroup\">{{t _select_device_group}}:</label>\n            {{view Ember.Select\n        contentBinding=\"FLOW.deviceGroupControl.content\"\n        selectionBinding=\"FLOW.selectedControl.selectedDeviceGroup\"\n        optionLabelPath=\"content.code\"\n        optionValuePath=\"content.keyId\"\n        id=\"deviceGroup\"\n        prompt=\"\"\n        promptBinding=\"Ember.STRINGS._select_device_group\"}}\n            </div>\n            <div class=\"formSelectorList\">\n              <nav>\n                <ul>\n                  <li><a {{action selectAllDevices target=\"this\"}}>{{t _select_all}}</a></li>\n                  <li><a {{action deselectAllDevices target=\"this\"}}>{{t _deselect_all}}</a></li>\n                </ul>\n              </nav>\n            <label for=\"devices\">{{t _select_devices}}:</label>\n            {{view Ember.Select\n             multiple=true\n             size=10\n          contentBinding=\"FLOW.devicesInGroupControl.arrangedContent\"\n          selectionBinding=\"FLOW.selectedControl.selectedDevices\"\n          optionLabelPath=\"content.combinedName\"\n          optionValuePath=\"content.keyId\"\n         id=\"devices\"}}\n        <a {{action addSelectedDevices target=\"this\"}} class=\"AddBtn\">{{t _add_selected_devices}}</a>\n            </div>\n          </fieldset>\n        </div>\n        <div class=\"formRightPanel\">\n          <fieldset id=\"devicesPreview\" class=\"floats-in\">\n            <h2>{{t _preview_device_selection}}:</h2>\n            <div class=\"\">\n              <!-- DEVICES TABLE-->\n              <table id=\"devicePreviewList\" class=\"previewList\" >\n                <!-- TABLE HEADER-->\n                <thead>\n                  <tr>\n                    <th class=\"groupPreview\">{{t _device_group}}</th>\n                    <th class=\"surveyPreview\">{{t _device}}</th>\n                    <th class=\"action\"></th>\n                  </tr>\n                </thead>\n                <!-- TABLE BODY: MAIN CONTENT-->\n                <tbody>\n                  {{#each device in view.devicesPreview}}\n                  <tr>\n                    <td class=\"deviceGroup\">{{device.deviceGroupName}}</td>\n                    <td class=\"deviceId\">{{device.combinedName}}</td>\n                    <td class=\"action\"><a {{action \"removeSingleDevice\" device target=\"this\"}} class=\"remove\">{{t _remove}}</a></td>\n                  </tr>\n                  {{/each}}\n                </tbody>\n                <!-- TABLE FOOTER-->\n                <tfoot>\n                  <tr>\n                    <td colspan=\"7\"><a {{action \"removeAllDevices\" target=\"this\"}}>{{t _clear_all}}</a></td>\n                  </tr>\n                </tfoot>\n              </table>\n            </div>\n          </fieldset>\n        </div>\n      </div>\n      <div class=\"menuConfirm\">\n        <ul>\n          <li><a {{action \"saveSurveyAssignment\" target=\"this\"}} class=\"standardBtn\">{{t _save_assignment}}</a></li>\n          <li><a {{action \"cancelEditSurveyAssignment\" target=\"this\"}} class=\"\">{{t _cancel}}</a></li>\n        </ul>\n      </div>\n    </form>\n  </div>\n</section>\n{{/view}}\n");

});

loader.register('akvo-flow/templates/navDevices/assignment-list-tab/assignment-list', function(require) {

return Ember.Handlebars.compile("<section class=\"assignmentsListed\" id=\"assignmentsList\">\n  <!-- assignments TABLE-->\n  \n  {{#view FLOW.AssignmentsListTabView}}\n  <div class=\"deviceControls\">\n    <a {{action \"createNewAssignment\" target=\"this\"}} class=\"btnOutline\">{{t _create_new_assignment}}</a></div>\n    <div id=\"devicesListTable_length\" class=\"dataTables_length\"> </div>\n    <table class=\"dataTable\" id=\"deviceDataTable\">\n      <!-- TABLE HEADER-->\n      <thead>\n        <tr> {{#view FLOW.ColumnView item=\"name\" type=\"assignment\"}} <a {{action \"sort\" target=\"this\"}}>{{t _name}}</a> {{/view}}\n        {{#view FLOW.ColumnView item=\"startDate\" type=\"assignment\"}} <a {{action \"sort\" target=\"this\"}}>{{t _start_date}}</a> {{/view}}\n        {{#view FLOW.ColumnView item=\"endDate\" type=\"assignment\"}} <a {{action \"sort\" target=\"this\"}}>{{t _end_date}}</a> {{/view}}\n        <th class=\"action noArrows\"> <a>{{t _action}}</a></th>\n      </tr>\n    </thead>\n    <!-- TABLE BODY: MAIN CONTENT-->\n    <tbody>\n      {{#each assignment in FLOW.surveyAssignmentControl}}\n      <tr>\n        <td class=\"name\" style=\"text-align:left; padding:0 0 0 5px;\">{{assignment.name}}</td>\n        <td class=\"startDate\" >{{#with assignment}} {{date3 startDate}} {{/with}}</td>\n        <td class=\"endDate\" >{{#with assignment}} {{date3 endDate}} {{/with}}</td>\n        <td class=\"action\"><a {{action \"editSurveyAssignment\" assignment target=\"this\"}} class=\"edit\">{{t _edit}}</a> {{#view FLOW.AssignmentView contentBinding=\"assignment\"}}<a {{action confirm FLOW.dialogControl.delAssignment target=\"FLOW.dialogControl\"}} class=\"remove\">{{t _delete}}</a>{{/view}}\n      </tr>\n      {{/each}}\n    </tbody>\n  </table>\n{{/view}} </section>");

});

loader.register('akvo-flow/templates/navDevices/bootstrap-tab/survey-bootstrap', function(require) {

return Ember.Handlebars.compile("{{#view FLOW.SurveyBootstrap}}\n<section class=\"fullWidth manualTransfer\" id=\"assignSurveys\">\n    <form>\n      <div class=\"fieldSetWrap floats-in\">\n      <div class=\"formLeftPanel\">\n        <fieldset id=\"surveySelect\" class=\"floats-in\">\n          <h2>01. {{t _select_survey}}:</h2>\n          <span class=\"infoText\">{{t _cant_find_your_survey_}}</span>\n          <div class=\"\">\n            <label for=\"surveyGroup\">{{t _select_survey}}:</label>\n            {{#unless FLOW.projectControl.isLoading}}\n              {{view FLOW.SurveySelectionView}}\n            {{/unless}}\n\t\t\t</div>\n          <div class=\"formSelectorList\">\n            <nav>\n              <ul>\n                <li><a {{action selectAllSurveys target=\"this\"}}>{{t _select_all}}</a></li>\n                <li><a {{action deselectAllSurveys target=\"this\"}}>{{t _deselect_all}}</a></li>\n              </ul>\n            </nav>\n            <label for=\"surveys\">{{t _select_forms}}:</label>\n           {{view Ember.Select\n           multiple=true\n           size=10\n        contentBinding=\"FLOW.surveyControl.publishedContent.arrangedContent\"\n        selectionBinding=\"FLOW.selectedControl.selectedSurveys\"\n        optionLabelPath=\"content.name\"\n          id=\"surveys\"\n        optionValuePath=\"content.keyId\"}}\n      <a {{action addSelectedSurveys target=\"this\"}}  class=\"AddBtn\">{{t _add_selected_forms}}</a>\n          </div>\n        </fieldset>\n      </div>\n      <div class=\"formRightPanel\">\n        <fieldset id=\"surveyPreview\" class=\"floats-in\">\n          <h2>{{t _preview_survey_selection}}:</h2>\n          <div class=\"\">\n            <!-- DEVICES TABLE-->\n            <table id=\"surveyPreviewList\" class=\"previewList\" >\n              <!-- TABLE HEADER-->\n              <thead>\n                <tr>\n                  <th class=\"groupPreview\">{{t _survey}}</th>\n                  <th class=\"surveyPreview\">{{t _form}}</th>\n                  <th class=\"action\"></th>\n                </tr>\n              </thead>\n              <!-- TABLE BODY: MAIN CONTENT-->\n              <tbody>\n                {{#each survey in view.surveysPreview}}\n                <tr>\n                  <td class=\"groupPreview\">{{survey.surveyGroupName}}</td>\n                  <td class=\"surveyPreview\"{{survey.name}}</td>\n                  <td class=\"action\"><a {{action \"removeSingleSurvey\" survey target=\"this\"}} class=\"remove\">{{t _remove}}</a></td>\n                </tr>\n                {{/each}}\n              </tbody>\n              <!-- TABLE FOOTER-->\n              <tfoot>\n                <tr>\n                  <td colspan=\"7\"><a {{action \"removeAllSurveys\" target=\"this\"}}>{{t _clear_all}}</a></td>\n                </tr>\n              </tfoot>\n            </table>\n          </div>\n        </fieldset>\n      </div>\n      </div>\n\n    <div class=\"fieldSetWrap makeWhite noBG\">\n        <fieldset id=\"devicesSelect\" class=\"fullWidth\">\n          <h2>02. {{t _notification_details}}:</h2>\n          <div class=\"\">\n          <label for=\"notificationEmail\">{{t _notification_email}}:</label>\n         {{view Ember.TextField\n                valueBinding=\"view.notificationEmail\"\n                id=\"notificationEmail\"\n                placeholder=\"\"\n                placeholderBinding=\"Ember.STRINGS._enter_notification_email\"\n                size=30}}\n         {{#if view.undef}}\n           <label for=\"includeDBInstructions\">{{t _include_db_instructions}}:</label>\n           {{view Ember.Checkbox checkedBinding=\"view.includeDBInstructions\" id=\"includeDBInstructions\"}}\n           {{#if view.includeDBInstructions}}\n             {{view Ember.TextArea valueBinding=\"view.dbInstructions\" id=\"dbInstructions\"}}\n           {{/if}}\n         {{/if}}\n        </fieldset>\n      </div>\n      <div class=\"menuConfirm\">\n        <ul>\n          <li><a {{action \"sendSurveys\" target=\"this\"}} class=\"standardBtn\">{{t _send_file}}</a></li>\n        </ul>\n      </div>\n    </form>\n</section>\n{{/view}}\n");

});

loader.register('akvo-flow/templates/navDevices/devices-list-tab/devices-list', function(require) {

return Ember.Handlebars.compile("<section id=\"devicesList\">\n  <!-- DEVICES TABLE-->\n  \n  {{#view FLOW.CurrentDevicesTabView}}\n  <div class=\"deviceControls\">\n        <a {{action \"showManageDeviceGroupsDialog\" target=\"this\"}} class=\"btnOutline\">{{t _manage_device_groups}}</a>\n        {{#if FLOW.deviceControl.atLeastOneSelected}}\n        <nav class=\"dataTabMenu\">\n          <ul>\n            <li><a {{action \"showAddToGroupDialog\" target=\"this\"}}>{{t _add_to_device_group}}</a></li>\n            <li><a {{action \"showRemoveFromGroupDialog\" target=\"this\"}}>{{t _remove_from_device_group}}</a></li>\n          </ul>\n        </nav>\n        {{else}}\n        <nav class=\"dataTabMenu\">\n          <ul>\n            <!-- <li><a href=\"#\" class=\"disabled\">{{t _disable_devices}}</a></li> -->\n            <li><a href=\"#\" class=\"disabled\">{{t _add_to_device_group}}</a></li>\n            <li><a href=\"#\" class=\"disabled\">{{t _remove_from_device_group}}</a></li>\n          </ul>\n        </nav>\n        {{/if}}\n      </div>\n      <table class=\"dataTable\" id=\"surveyDataTable\">\n        <!-- TABLE HEADER-->\n        <thead>\n          <tr> {{#view FLOW.ColumnView item=\"select\" }}\n            {{view Ember.Checkbox checkedBinding=\"FLOW.deviceControl.allAreSelected\"}}\n            {{/view}}\n            {{#view FLOW.ColumnView item=\"IMEI\" type=\"device\"}} <a {{action \"sort\" target=\"this\"}}>{{t _imei}} {{tooltip _imei_tooltip}}</a> {{/view}}\n            {{#view FLOW.ColumnView item=\"deviceIdentifier\" type=\"device\"}} <a {{action \"sort\" target=\"this\"}}>{{t _device_id}}</a> {{/view}}\n            {{#view FLOW.ColumnView item=\"deviceGroup\" type=\"device\"}} <a {{action \"sort\" target=\"this\"}}>{{t _device_group}}</a> {{/view}}\n            {{#view FLOW.ColumnView item=\"lastPositionDate\" type=\"device\"}} <a {{action \"sort\" target=\"this\"}}>{{t _last_contact}}</a> {{/view}}\n            {{#view FLOW.ColumnView item=\"lastPositionDate\" type=\"device\"}} <a {{action \"sort\" target=\"this\"}}>{{t _version}}</a> {{/view}} </tr>\n          </thead>\n          <!-- TABLE BODY: MAIN CONTENT-->\n          <tbody>\n            {{#each FLOW.deviceControl}}\n            <tr>\n              <td class=\"selection\"> {{view Ember.Checkbox checkedBinding=\"isSelected\"}}</td>\n              <td class=\"EMEI\">{{esn}}</td>\n              <td class=\"deviceId\" >{{deviceIdentifier}}</td>\n              <td class=\"deviceGroup\">{{deviceGroupName}}</td>\n              <td class=\"lastBeacon\">{{date1 lastPositionDate}}</td>\n              <td class=\"version\">{{gallatinSoftwareManifest}}</td>\n            </tr>\n            {{/each}}\n          </tbody>\n        </table>\n        <!--     {{#if view.showConfirmDeletedialog}}\n        <a {{action \"doDelete\" target=\"this\"}}>{{t _ok}}</a>\n        <a {{action \"cancelDelete\" target=\"this\"}}>{{t _cancel}}</a>\n        {{/if}}     -->\n        \n        <!-- add to group dialog-->\n        <div {{bindAttr class=\"view.showAddToGroupDialogBool:display :overlay\"}}>\n          <div class=\"blanket\"></div>\n          <div class=\"dialogWrap\">\n            <!-- the dialog contents -->\n            <div class=\"confirmDialog dialog\">\n              <h2>{{t _add_devices_to_device_group}}</h2>\n              <p class=\"dialogMsg\">{{t _choose_an_existing_device_group_from_the_list}}</p>\n              <br/>\n              {{view Ember.Select\n              contentBinding=\"FLOW.deviceGroupControl.contentNoUnassigned\"\n              selectionBinding=\"view.selectedDeviceGroup\"\n              optionLabelPath=\"content.code\"\n              optionValuePath=\"content.keyId\"\n              prompt=\"\"\n              promptBinding=\"Ember.STRINGS._select_existing_device_group\"}}\n              <div class=\"buttons menuCentre\">\n                <ul>\n                  <li><a {{action \"doAddToGroup\" target=\"this\"}} class=\"ok smallBtn\">{{t _ok}}</a></li>\n                  <li><a {{action \"cancelAddToGroup\" target=\"this\"}}} class=\"cancel\">{{t _cancel}}</a></li>\n                </ul>\n              </div>\n            </div>\n          </div>\n        </div>\n\n        <!-- Remove from group dialog-->\n        <div {{bindAttr class=\"view.showRemoveFromGroupDialogBool:display :overlay\"}}>\n          <div class=\"blanket\"></div>\n          <div class=\"dialogWrap\">\n            <!-- the dialog contents -->\n            <div class=\"confirmDialog dialog\">\n              <h2>{{t _remove_devices_from_device_group}}?</h2>\n              <div class=\"buttons menuCentre\">\n                <ul>\n                  <li><a {{action \"doRemoveFromGroup\" target=\"this\"}} class=\"ok smallBtn\">{{t _ok}}</a></li>\n                  <li><a {{action \"cancelRemoveFromGroup\" target=\"this\"}}} class=\"cancel\">{{t _cancel}}</a></li>\n                </ul>\n              </div>\n            </div>\n          </div>\n        </div>\n\n        <!-- manage device groups dialog-->\n        <div {{bindAttr class=\"view.showManageDeviceGroupsDialogBool:display :overlay\"}}>\n          <div class=\"blanket\"></div>\n          <div class=\"dialogWrap\">\n            <!-- the dialog contents -->\n            <div class=\"confirmDialog dialog\">\n              <h2>{{t _manage_device_groups}}</h2>\n              <p class=\"dialogMsg\">{{t _to_change_the_name_of_an_existing_group_}}</p>\n              <br/>\n              {{view Ember.Select\n              contentBinding=\"FLOW.deviceGroupControl.contentNoUnassigned\"\n              selectionBinding=\"view.selectedDeviceGroup\"\n              optionLabelPath=\"content.code\"\n              optionValuePath=\"content.keyId\"\n              prompt=\"\"\n              promptBinding=\"Ember.STRINGS._select_device_group\"}}\n\n              {{view Ember.TextField valueBinding=\"view.changedDeviceGroupName\" size=40}}\n              <p class=\"dialogMsg\">{{t _to_create_a_new_device_group_}}</p>\n              <label for=\"newDeviceGroupName\">{{t _new_group}}:</label>\n              {{view Ember.TextField valueBinding=\"view.newDeviceGroupName\" id=\"newDeviceGroupName\" size=40}}\n              <p class=\"dialogMsg\">{{t _to_delete_an_existing_group_}}</p>\n              {{view Ember.Select\n              contentBinding=\"FLOW.deviceGroupControl.contentNoUnassigned\"\n              selectionBinding=\"view.selectedDeviceGroupForDelete\"\n              optionLabelPath=\"content.code\"\n              optionValuePath=\"content.keyId\"\n              prompt=\"\"\n              promptBinding=\"Ember.STRINGS._select_device_group\"}}\n              <a {{action confirm FLOW.dialogControl.delDeviceGroup target=\"FLOW.dialogControl\"}} class=\"remove\">{{t _remove}}</a>\n              <div class=\"buttons menuCentre\">\n                <ul>\n                  <li><a {{action \"doManageDeviceGroups\" target=\"this\"}} class=\"ok smallBtn\">{{t _save}}</a></li>\n                  <li><a {{action \"cancelManageDeviceGroups\" target=\"this\"}}} class=\"cancel\">{{t _cancel}}</a></li>\n                </ul>\n              </div>\n            </div>\n          </div>\n        </div>\n        {{/view}}\n      </section>");

});

loader.register('akvo-flow/templates/navDevices/devices-subnav', function(require) {

return Ember.Handlebars.compile("<ul>\n    {{#view view.NavItemView item=\"currentDevices\" }}\n    \t<a {{action doCurrentDevices}}>{{t _devices_list}}</a>\n    {{/view}} \n    {{#view view.NavItemView item=\"assignSurveys\" }}\n    \t<a {{action doAssignSurveysOverview}}>{{t _assignments_list}}</a>\n    {{/view}} \n    {{#view view.NavItemView item=\"surveyBootstrap\" }}\n      <a {{action doSurveyBootstrap}}>{{t _manual_survey_transfer}}</a>\n    {{/view}} \n</ul>\n");

});

loader.register('akvo-flow/templates/navDevices/nav-devices', function(require) {

return Ember.Handlebars.compile("<section class=\"topBar\">\n    <div id=\"tabs\">\n        <nav class=\"tabNav floats-in\">\n            {{view FLOW.DevicesSubnavView controllerBinding=\"controller.controllers.devicesSubnavController\"}}\n        </nav>    \n    </div>\n</section>    \n<section class=\"devicesSection floats-in belowHeader\" id=\"main\" role=\"main\">\n    {{outlet}}\n</section>\n");

});

loader.register('akvo-flow/templates/navMaps/geoshape-map', function(require) {

return Ember.Handlebars.compile("<div class=\"geoshapeMapContainer\" style=\"width:99%; float: left\"></div>\n{{#if view.isPolygon}}\n  <div  style=\"float: left; width: 100%\">\n    <div style=\"float: left; width: 100%\">{{t _points}}: {{view.pointCount}}</div>\n    <div style=\"float: left; width: 100%\">{{t _length}}: {{view.length}}m</div>\n    <div style=\"float: left; width: 100%\">{{t _area}}: {{view.area}}m&sup2;</div>\n  </div>\n{{else}} {{#if view.isLineString}}\n  <div  style=\"float: left; width: 100%\">\n    <div style=\"float: left; width: 100%\">{{t _points}}: {{view.pointCount}}</div>\n    <div style=\"float: left; width: 100%\">{{t _length}}: {{view.length}}m</div>\n  </div>\n{{else}} {{#if view.isMultiPoint}}\n  <div  style=\"float: left; width: 100%\">\n    <div style=\"float: left; width: 100%\">{{t _points}}: {{view.pointCount}}</div>\n  </div>\n{{else}}\n    <div> {{view.geoshapeString}} </div>\n{{/if}}{{/if}}{{/if}}\n");

});

loader.register('akvo-flow/templates/navMaps/nav-maps-common', function(require) {

return Ember.Handlebars.compile("<section id=\"main\" class=\"mapFlow floats-in middleSection\" role=\"main\">\n  {{#if view.allowFilters}}\n      {{#unless FLOW.projectControl.isLoading}}\n        {{view FLOW.SurveySelectionView}}\n      {{/unless}}\n      {{#if FLOW.selectedControl.selectedSurveyGroup}}\n        {{view Ember.Select\n            contentBinding=\"FLOW.surveyControl.arrangedContent\"\n            selectionBinding=\"view.selectedSurvey\"\n            optionLabelPath=\"content.code\"\n            optionValuePath=\"content.keyId\"\n            prompt=\"\"\n            promptBinding=\"Ember.STRINGS._select_form\"\n            classNames=\"form-selector\"\n        }}\n      {{/if}}\n  {{/if}}\n  <div id=\"dropdown-holder\">\n    <div id=\"mapDetailsHideShow\" class=\"drawHandle hideMapD\"></div>\n  </div>\n  <div id=\"flowMap\"></div>\n  {{#view FLOW.PlacemarkDetailView controllerBinding=\"FLOW.placemarkDetailController\"}}\n    <div id=\"pointDetails\">\n      {{#if content}}\n        <ul class=\"placeMarkBasicInfo floats-in\">\n            {{#if view.cartoMaps}}\n            <h3>{{surveyedLocaleDisplayName}}</h3>\n            <li>\n                <span>{{t _data_point_id}}:</span>\n                <label style=\"display: inline; margin: 0 0 0 5px;\">{{surveyedLocaleIdentifier}}</label>\n            </li><br>\n            {{/if}}\n          <li>\n            <span>{{t _collected_on}}:</span>\n            <div class=\"placeMarkCollectionDate\">\n              {{date2 collectionDate}}\n            </div>\n          </li>\n          <li></li>\n        </ul>\n        <div class=\"mapInfoDetail\">\n          {{#each arrangedContent}}\n            <p>{{placemarkDetail}}</p>\n            {{drawGeoshapes}}\n          {{else}}\n            <p class=\"noDetails\">{{t _no_details}}</p>\n          {{/each}}\n        </div>\n      {{else}}\n        <p class=\"noDetails\">{{t _no_details}}</p>\n      {{/if}}\n\n    </div>\n  {{/view}}\n  <div id=\"flowMapLegend\">\n    <h1>{{t _legend}}</h1>\n  </div>\n</section>\n\n<style>\n  #pointDetails > dl > div.defListWrap:nth-child(odd) {\n    background-color: rgb(204,214,214);\n  }\n</style>\n<script type=\"text/javascript\">\n(function(){\n\n  var dropDown = document.querySelector('#dropdown-holder');\n  var header = document.querySelector('header');\n  var footer = document.querySelector('footer');\n\n  function resizeMap() {\n\n    if (!document.querySelector('#flowMap')) {\n      // If we can't find the map element, assume we have changed tabs and remove listener\n      window.removeEventListener('resize', resizeMap);\n      return;\n    }\n\n    var totalHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);\n    var newHeight = totalHeight;\n\n    newHeight -= dropDown.offsetHeight;\n    newHeight -= header.offsetHeight;\n    newHeight -= footer.offsetHeight;\n    newHeight = newHeight * 0.95;\n\n    document.querySelector('#flowMap').style.height = newHeight + 'px';\n    document.querySelector('#pointDetails').style.height = newHeight + 'px';\n  }\n\n  window.addEventListener('resize', resizeMap);\n  resizeMap();\n})()\n</script>\n");

});

loader.register('akvo-flow/templates/navMessages/nav-messages', function(require) {

return Ember.Handlebars.compile("{{#view FLOW.MessagesListView}}\n<div class=\"middleSection\">\n  <section id=\"\" class=\"fullWidth messagesList\"> \n    <!-- Messages TABLE-->\n    <table id=\"messageListTable\" class=\"dataTable headerFixed\" >\n      <!-- TABLE HEADER-->\n      <thead>\n        <tr>\n           {{#view FLOW.ColumnView item=\"lastUpdateDateTime\" type=\"message\"}}\n                  <a {{action \"sort\" target=\"this\"}} class=\"date\">{{t _date}}</a>\n           {{/view}}\n           {{#view FLOW.ColumnView item=\"objectId\" type=\"message\"}}\n                  <a {{action \"sort\" target=\"this\"}} class=\"objectId\">{{t _form_id}}</a>\n           {{/view}}\n            {{#view FLOW.ColumnView item=\"objectTitle\" type=\"message\"}}\n                  <a {{action \"sort\" target=\"this\"}} class=\"survey\">{{t _survey}}</a>\n           {{/view}}\n           {{#view FLOW.ColumnView item=\"actionAbout\" type=\"message\"}}\n                  <a {{action \"sort\" target=\"this\"}} class=\"type\">{{t _type}}</a>\n           {{/view}}\n           {{#view FLOW.ColumnView item=\"shortMessage\" type=\"message\"}}\n                  <a {{action \"sort\" target=\"this\"}} class=\"message\">{{t _message}}</a>\n           {{/view}}\n        </tr>\n      </thead>\n      <!-- TABLE BODY: MAIN CONTENT-->\n      <tbody>\n        {{#each mess in FLOW.messageControl}}\n          <tr>\n            <td class=\"date\" style=\"text-align:left; padding:0 0 0 20px;\">{{#with mess}}{{date1 lastUpdateDateTime}}{{/with}}</td>\n             <td class=\"actionAbout\">{{unbound mess.objectId}}</td>\n            <td class=\"objectTitle\">{{unbound mess.objectTitle}}</td>\n            <td class=\"actionAbout\">{{unbound mess.actionAbout}}</td>\n            <td class=\"message\" style=\"text-align:left; padding:0 0 0 20px;\">{{unbound mess.shortMessage}}</td>\n          </tr>\n        {{/each}}\n      </tbody>\n    </table>\n\n  </section>\n</div>\n\n{{/view}}");

});

loader.register('akvo-flow/templates/navReports/applets/comprehensive-report-applet', function(require) {

return Ember.Handlebars.compile("<div class=\"block\">\n<applet height=\"30\" width=\"100\"\n\tcode=\"com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl\"\n\tarchive=\"../exporterapplet.jar,../json.jar,../jcommon-1.0.16.jar,../jfreechart-1.0.13.jar,../poi-3.8-20120326.jar,../poi-ooxml-3.8-20120326.jar,../poi-ooxml-schemas-3.8-20120326.jar,../xbean.jar,../dom4j-1.6.1.jar,../gdata-core-1.0.jar\">\n\t<param name=\"cache-archive\" value=\"exporterapplet.jar,json.jar\">\n    <param name=\"cache-version\" value=\"1.3,1.0\">\n\t<param name=\"exportType\" value=\"GRAPHICAL_SURVEY_SUMMARY\">\n\t<param name=\"java_arguments\" value=\"-Xmx1024m\">\n\t<param name=\"factoryClass\"\n\t\tvalue=\"org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory\">\n\t<param name=\"criteria\" value=\"surveyId:={{unbound FLOW.selectedControl.selectedSurvey.keyId}}\">\n    <param name=\"serverOverride\" value=\"{{getServer}}\">\n\t<!-- FIXME locale must be configurable -->\n\t<param name=\"options\"\n\t\tvalue=\"locale:=en;performRollup:={{unbound FLOW.editControl.summaryPerGeoArea}};nocharts:={{unbound FLOW.editControl.summaryPerGeoArea}};imgPrefix:={{unbound FLOW.Env.photo_url_root}}/\">\n</applet>\n</div>");

});

loader.register('akvo-flow/templates/navReports/applets/google-earth-file-applet', function(require) {

return Ember.Handlebars.compile("<div class=\"block\">\n<applet height=\"30\" width=\"100\"\n\tcode=\"org.waterforpeople.mapping.dataexport.KMLApplet\"\n\tarchive=\"../exporterapplet.jar,../json.jar,../poi-3.5-signed.jar,../velocity-1.6.2-dep.jar,../gdata-core-1.0.jar\">\n\t<param name=\"cache-archive\"\n\t\tvalue=\"exporterapplet.jar,json.jar,poi-3.5-signed.jar,velocity-1.6.2-dep.jar\">\n\t<param name=\"cache-version\" value=\"1.3,1.0,3.5\">\n    <param name=\"serverOverride\" value=\"{{getServer}}\">\n</applet>\n</div>");

});

loader.register('akvo-flow/templates/navReports/applets/raw-data-report-applet', function(require) {

return Ember.Handlebars.compile("<div class=\"block\">\n<applet width=\"100\" height=\"30\"\n  code=\"com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl\"\n  archive=\"../exporterapplet.jar,../json.jar,../jcommon-1.0.16.jar,../jfreechart-1.0.13.jar,../poi-3.8-20120326.jar,../poi-ooxml-3.8-20120326.jar,../poi-ooxml-schemas-3.8-20120326.jar,../xbean.jar,../dom4j-1.6.1.jar,../gdata-core-1.0.jar\">\n  <param name=\"cache-archive\" value=\"exporterapplet.jar,json.jar\">\n  <param name=\"cache-version\" value=\"1.3,1.0\">\n  <param name=\"exportType\" value=\"RAW_DATA\">\n  <param name=\"java_arguments\" value=\"-Xmx1024m\">\n  <param name=\"factoryClass\"\n    value=\"org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory\">\n  <param name=\"criteria\" value=\"surveyId:={{unbound FLOW.selectedControl.selectedSurvey.keyId}}\">\n  <param name=\"serverOverride\" value=\"{{getServer}}\">\n  <!-- FIXME locale must be configurable -->\n  <param name=\"options\"\n    value=\"exportMode:=RAW_DATA;locale:=en;imgPrefix:={{unbound FLOW.Env.photo_url_root}};generateTabFormat=false\"/>\n</applet>\n</div>\n");

});

loader.register('akvo-flow/templates/navReports/applets/survey-form-applet', function(require) {

return Ember.Handlebars.compile("<div class=\"block\">\n<applet height=\"30\" width=\"100\"\n\tcode=\"com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl\"\n\tarchive=\"../exporterapplet.jar,../json.jar,../poi-3.5-signed.jar,../gdata-core-1.0.jar\">\n\t<param name=\"cache-archive\"\n\t\tvalue=\"exporterapplet.jar,json.jar,poi-3.5-signed.jar\">\n\t<param name=\"cache-version\" value=\"1.3,1.0,3.5\">\n\t<param name=\"exportType\" value=\"SURVEY_FORM\">\n\t<param name=\"factoryClass\"\n\t\tvalue=\"org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory\">\n\t<param name=\"criteria\" value=\"surveyId:={{unbound FLOW.selectedControl.selectedSurvey.keyId}}\">\n    <param name=\"serverOverride\" value=\"{{getServer}}\">\n</applet>\n</div>");

});

loader.register('akvo-flow/templates/navReports/chart-reports', function(require) {

return Ember.Handlebars.compile(" <section class=\"fullWidth reportTools\" id=\"reportBlocks\">\n    {{#view FLOW.chartView}}\n      {{#unless FLOW.projectControl.isLoading}}\n        {{view FLOW.SurveySelectionView}}\n      {{/unless}}\n      <span class=\"\"></span>\n      {{#if FLOW.selectedControl.selectedSurvey}}\n        {{view Ember.Select\n            contentBinding=\"FLOW.surveyControl.arrangedContent\"\n            selectionBinding=\"view.selectedSurvey\"\n            optionLabelPath=\"content.name\"\n            optionValuePath=\"content.keyId\"\n            prompt=\"\"\n            promptBinding=\"Ember.STRINGS._select_form\"}}\n\n          {{#if view.selectedSurvey}}\n            {{view Ember.Select\n                contentBinding=\"FLOW.questionControl.OPTIONcontent\"\n                selectionBinding=\"FLOW.selectedControl.selectedQuestion\"\n                optionLabelPath=\"content.text\"\n                optionValuePath=\"content.keyId\"\n                prompt=\"\"\n                promptBinding=\"Ember.STRINGS._select_question\"}}\n          {{/if}}\n      {{/if}}\n<div class=\"chartSetting\">\n<h4>{{t _chart_type}}:</h4>\n      {{view Ember.Select\n        contentBinding=\"FLOW.chartTypeControl.content\"\n        selectionBinding=\"view.chartType\"\n        optionLabelPath=\"content.label\"\n        optionValuePath=\"content.value\"\n        prompt=\"\"\n        promptBinding=\"Ember.STRINGS._select_chart_type\"}}\n\n{{#if view.isDoughnut}}\n  <label class=\"groupChartSelect\"> {{t _put_smaller_items_together}} {{view Ember.Checkbox checkedBinding=\"view.compactSmaller\"}}</label>\n{{/if}}\n        <a {{action getChartData target=\"this\"}} class=\"smallBtn\"> {{t _build_chart}} </a>\n</div>\n    {{#if view.noChoiceBool}}\n      <p class=\"errorMsg\">{{t _please_select_survey_group_survey_and_question}}</p>\n    {{/if}}\n     {{#if view.noDataBool}}\n      <p class=\"errorMsg\">{{t _there_is_no_data_available_for_question}}</p>\n    {{/if}}\n     \t<h3 class=\"chartTitle\">{{FLOW.selectedControl.selectedQuestion.text}}</h3>\n    {{#unless view.hideChart}}\n        <div id=\"piechart\" class=\"chartPie\">\n\t\t\t<p>{{t _choose_a_question}}</p>\n\t\t</div>\n    {{/unless}}\n{{/view}}\n\n <script type=\"text/javascript\">\n\nfunction deleteChart(){\n  $('#piechart').empty();\n}\n\nfunction createDoughnutChart(){\n    var canvasWidth = 1000, //width\n      canvasHeight = 400,   //height\n      outerRadius = 100,   //radius\n      labelRadius = 120,   //radius\n      color = d3.scale.category20(); //builtin range of colors\n\n    var dataSet = FLOW.chartDataControl.get('chartData');\n    var smallerItems = FLOW.chartDataControl.get('smallerItems');\n    var total = FLOW.chartDataControl.get('total');\n\n    var vis = d3.select(\"#piechart\")\n      .append(\"svg:svg\") //create the SVG element inside the <body>\n        .data([dataSet]) //associate our data with the document\n        .attr(\"width\", canvasWidth) //set the width of the canvas\n        .attr(\"height\", canvasHeight) //set the height of the canvas\n        .append(\"svg:g\") //make a group to hold our pie chart\n        .attr(\"transform\", \"translate(\" + 0.3*canvasWidth + \",\" + 0.4*canvasHeight + \")\") // relocate center of pie\n\n    // This will create <path> elements for us using arc data...\n    var arc = d3.svg.arc()\n      .outerRadius(outerRadius)\n      .innerRadius(outerRadius-50);\n\n    var pie = d3.layout.pie() //this will create arc data for us given a list of values\n      .value(function(d) { return d.percentage; }) // Binding each value to the pie\n      .sort( function(d) { return null; } );\n\n      vis.append(\"svg:text\")\n      .attr(\"text-anchor\", \"left\")\n      .style(\"fill\", \"rgb(246, 160, 26)\")\n      .style(\"font\", \"bold 1.5em helvetica\")\n      .text(Ember.String.loc('_smallest_items'))\n      .attr(\"transform\",\"translate(320,-120)\");\n\n\n      vis.selectAll(\"p\")\n      .data(smallerItems)\n      .enter()\n      .append(\"svg:text\")\n      .attr(\"text-anchor\", \"left\")\n      .style(\"fill\", \"rgb(58,58,58)\")\n      .style(\"font\", \"bold 1.1em helvetica\")\n      .text(function(d){\n        return d.legendLabel;\n      })\n      .attr(\"transform\",function(d,i){\n        return \"translate(325,\" + (-100+i*20) + \")\";\n      })\n\n    // Select all <g> elements with class slice (there aren't any yet)\n    var arcs = vis.selectAll(\"g.slice\")\n      // Associate the generated pie data (an array of arcs, each having startAngle,\n      // endAngle and value properties)\n      .data(pie)\n      // This will create <g> elements for every \"extra\" data element that should be associated\n      // with a selection. The result is creating a <g> for every object in the data array\n      .enter()\n      // Create a group to hold each slice (we will have a <path> and a <text>\n      // element associated with each slice)\n      .append(\"svg:g\")\n      .attr(\"class\", \"slice\");    //allow us to style things in the slices (like text)\n\n    arcs.append(\"svg:path\")\n      //set the color for each slice to be chosen from the color function defined above\n      .attr(\"fill\", function(d, i) { return color(i); } )\n      //this creates the actual SVG path using the associated data (pie) with the arc drawing function\n      .attr(\"d\", arc);\n\n    // Add a legendLabel to each arc slice...\n    arcs.append(\"svg:text\")\n      .attr(\"transform\", function(d) {\n        var c = arc.centroid(d),\n          x = c[0],\n          y = c[1],\n          // pythagorean theorem for hypotenuse\n          h = Math.sqrt(x*x + y*y);\n          return \"translate(\" + (x/h * labelRadius) +  ',' + (y/h * labelRadius) +  \")\";\n        })\n      .attr(\"text-anchor\", function(d) {\n        // are we past the center?\n        return (d.endAngle + d.startAngle)/2 > Math.PI ? \"end\" : \"start\";\n      })\n      .style(\"fill\", \"rgb(58,58,58)\")\n      .style(\"font\", \"bold 1.1em helvetica\")\n      .html(function(d, i) { \n        var label = dataSet[i].legendLabel;\n\n        // If this label is on the left, wrap the text to keep it inside the SVG\n        if (d3.select(this).attr('text-anchor') === 'end') {\n\n          // The maximum number of characters per line\n          var wrapLength = 28;\n          var labelArray = [];\n          var tspanArray = [];          \n          var tspanIndex = 0;\n          var addSpace;\n\n          if (label.length > wrapLength) {\n            labelArray = label.split(' ');\n\n            // Group words in the label into lines less than wrapLength long\n            while (labelArray.length > 0) {\n\n              // assume this isn't the first word in this tspan so we don't need a space\n              addSpace = true;\n\n              if (!tspanArray[tspanIndex]) {\n                tspanArray[tspanIndex] = '';\n                addSpace = false;\n              }\n\n              if (tspanArray[tspanIndex].length + labelArray[0].length + 1 > wrapLength) {\n                tspanIndex++;\n                tspanArray[tspanIndex] = '';\n                addSpace = false;\n              }\n\n              if (addSpace) {\n                tspanArray[tspanIndex] += ' ';\n              }\n\n              tspanArray[tspanIndex] += labelArray[0];\n\n              // remove the word we just processed\n              labelArray.shift();\n            }\n\n            label = '';\n            for (var i = 0; i < tspanArray.length; i++) {\n              tspanArray[i] = '<tspan x=\"0\" dy=\"15\">' + tspanArray[i] + '</tspan>';\n              label += tspanArray[i];\n            }\n          }\n        }\n\n        return label; \n      }); //get the label from our original data array\n\n       // Add a legendLabel to each arc slice...\n    vis.append(\"svg:text\")\n      .attr(\"text-anchor\", \"middle\")\n      .style(\"fill\", \"rgb(58,58,58)\")\n      .style(\"font\", \"bold 1.2em helvetica\")\n      .text(\"Total:\") //get the label from our original data array\n      .attr(\"transform\",\"translate(0,-15)\");\n\n    vis.append(\"svg:text\")\n      .attr(\"text-anchor\", \"middle\")\n      .style(\"fill\", \"rgb(58,58,58)\")\n      .style(\"font\", \"bold 1.1em helvetica\")\n      .text(Ember.String.loc('_answers')) //get the label from our original data array\n      .attr(\"transform\",\"translate(0,15)\");\n\n    vis.append(\"svg:text\")\n      .attr(\"text-anchor\", \"middle\")\n      .style(\"fill\", \"rgb(58,58,58)\")\n      .style(\"font\", \"bold 1.1em helvetica\")\n      .text(total.toString())\n      .attr(\"transform\",\"translate(0,0)\");\n\n    // Computes the angle of an arc, converting from radians to degrees.\n    function angle(d) {\n      var a = (d.startAngle + d.endAngle) * 90 / Math.PI - 90;\n      return a > 90 ? a - 180 : a;\n    }\n}\n\nfunction createVBarChart(){\n// as in http://bl.ocks.org/3885304\nvar margin = {top: 20, right: 20, bottom: 30, left: 40},\n    width = 700 - margin.left - margin.right,\n    height = 500 - margin.top - margin.bottom;\n\n var dataSet = FLOW.chartDataControl.get('chartData');\n var maxPer = FLOW.chartDataControl.get('maxPer');\n var total = FLOW.chartDataControl.get('total');\n\nvar formatPercent = d3.format(\".0%\");\n\n// create linear scale for y axis\nvar yScaleMax = maxPer/100 + 0.1;\nif (yScaleMax > 1) yScaleMax = 1;\nvar y = d3.scale.linear()\n    .domain([0,yScaleMax])\n    .range([height*0.6,0]);\n\n// y axis\nvar yAxis = d3.svg.axis()\n    .scale(y)\n    .orient(\"left\")\n    .tickFormat(formatPercent);\n\n// add svg canvas to DOM\nvar svg = d3.select(\"#piechart\")\n    .append(\"svg\")\n    .attr(\"width\", width + margin.left + margin.right)\n    .attr(\"height\", height + margin.top + margin.bottom)\n    .append(\"g\")\n    .attr(\"transform\", \"translate(\" + margin.left + \",\" + margin.top + \")\");\n\n\n  // y axis\n  svg.append(\"g\")\n      .attr(\"class\", \"y axis\")\n      .call(yAxis)\n      .append(\"text\")\n      .attr(\"transform\", \"rotate(-90)\")\n      .attr(\"y\", 6)\n      .attr(\"dy\", \"0.71em\")\n      .style(\"text-anchor\", \"end\")\n      .text(\"Percentage\");\n\n  // add bars\n  svg.selectAll(\".bar\")\n      .data(dataSet)\n      .enter()\n      .append(\"svg:rect\")\n      .attr(\"class\", \"bar\")\n      .attr(\"x\", function(d,i){return 20+i*40;})\n      .attr(\"width\", 20)\n      .attr(\"y\",function(d){return y(d.percentage/100);})\n      .attr(\"height\",function(d){return height*0.6-y(d.percentage/100);})\n\n  // add labels\n  svg.selectAll(\"p\")\n      .data(dataSet)\n      .enter()\n      .append(\"svg:text\")\n      .attr(\"text-anchor\", \"left\")\n      .style(\"fill\", \"rgb(58,58,58)\")\n      .style(\"font\", \"bold 1.1em helvetica\")\n      .text(function(d){\n        return d.legendLabel;\n      })\n    .attr(\"transform\",function(d,i){\n         return \"translate(\" + (25+i*40) + \",\" + (0.6*height + 10) +\") rotate(45) \";\n       })\n\n    // add numbers on top of bars\n    svg.selectAll(\"num\")\n      .data(dataSet)\n      .enter()\n      .append(\"svg:text\")\n      .attr(\"text-anchor\", \"left\")\n      .style(\"fill\", \"rgb(0,0,0)\")\n      .style(\"font\", \"normal 1em helvetica\")\n      .html(function(d){\n        var num = d.percentage;\n        var output = \"<tspan>\" + num.toFixed(1).toString() + \"%\" + \"</tspan>\";\n        output += \"<tspan x='0' dy='15'>(\" + d.itemCount + \")</tspan>\";\n\n        return output; \n      })\n      .attr(\"transform\",function(d,i){\n        return \"translate(\" + (20+i*40) + \",\" + (y(d.percentage/100)-20) + \")\";\n      })\n\n    // \"Total answers\" label\n    svg.append(\"svg:text\")\n      .attr(\"text-anchor\", \"middle\")\n      .style(\"fill\", \"rgb(58,58,58)\")\n      .style(\"font\", \"bold 1.1em helvetica\")\n      .text(\"Total: \" + total.toString() + \" \" + Ember.String.loc('_answers'))\n      .attr(\"transform\",\"translate(140,340)\"); \n  }\n\n\n  function createHBarChart(){\n// as in http://bl.ocks.org/3885304\nvar margin = {top: 20, right: 20, bottom: 30, left: -10},\n    width = 700 - margin.left - margin.right,\n    height = 500 - margin.top - margin.bottom;\n\n var dataSet = FLOW.chartDataControl.get('chartData');\n var maxPer = FLOW.chartDataControl.get('maxPer');\n var total = FLOW.chartDataControl.get('total'); \n\nvar formatPercent = d3.format(\".0%\");\n\n// Vertical offset for the \"Total:\" label\nvar tLabelY = 30;\n\n// create linear scale for y axis\nvar yScaleMax = maxPer/100 + 0.1;\nif (yScaleMax > 1) yScaleMax = 1;\nvar y = d3.scale.linear()\n    .domain([0,yScaleMax])\n    .range([0,width*0.6]);\n\n// add svg canvas to DOM\nvar svg = d3.select(\"#piechart\")\n    .append(\"svg\")\n    .attr(\"width\", width + margin.left + margin.right)\n    .attr(\"height\", height + margin.top + margin.bottom)\n    .append(\"g\")\n    .attr(\"transform\", \"translate(\" + margin.left + \",\" + margin.top + \")\");\n\n svg.append(\"svg:text\")\n      .attr(\"text-anchor\", \"left\")\n      .style(\"fill\", \"rgb(246, 160, 26)\")\n      .style(\"font\", \"bold 2em helvetica\")\n      .text(\"Percentage\")\n      .attr(\"transform\",\"translate(300,0)\");\n\n  // \"Total answers\" label  \n  svg.append(\"svg:text\")\n    .attr(\"text-anchor\", \"left\")\n    .style(\"fill\", \"rgb(58,58,58)\")\n    .style(\"font\", \"bold 1.1em helvetica\")\n    .text(\"Total: \" + total.toString() + \" \" + Ember.String.loc('_answers'))\n    .attr(\"transform\",\"translate(300, \" + tLabelY + \")\"); \n\n  // add bars\n  svg.selectAll(\".bar\")\n      .data(dataSet)\n      .enter()\n      .append(\"svg:rect\")\n      .attr(\"class\", \"bar\")\n      .attr(\"y\", function(d,i){return 20+tLabelY+i*40;})\n      .attr(\"height\", 20)\n      .attr(\"x\",function(d){return 300;})\n      .attr(\"width\",function(d){return y(d.percentage/100);})\n\n  // add labels\n  svg.selectAll(\"p\")\n      .data(dataSet)\n      .enter()\n      .append(\"svg:text\")\n      .attr(\"text-anchor\", \"end\")\n      .style(\"fill\", \"rgb(58,58,58)\")\n      .style(\"font\", \"bold 1.1em helvetica\")\n      .text(function(d){\n        return d.legendLabel;\n      })\n    .attr(\"transform\",function(d,i){\n         return \"translate(280,\" + (35+tLabelY+i*40) +\")\";\n       })\n\n    // add numbers on top of bars\n    svg.selectAll(\"num\")\n      .data(dataSet)\n      .enter()\n      .append(\"svg:text\")\n      .attr(\"text-anchor\", \"left\")\n      .style(\"fill\", \"rgb(0,0,0)\")\n      .style(\"font\", \"normal 1.1em helvetica\")\n      .text(function(d){\n        var num = d.percentage;\n        return num.toFixed(1).toString() + \"%\" + \" (\" + d.itemCount + \")\";\n      })\n      .attr(\"transform\",function(d,i){\n        return \"translate(\" + (305+y(d.percentage/100)) + \",\" + (35+tLabelY+i*40) + \")\";\n      })\n  }\n\n    </script>\n    </section>\n");

});

loader.register('akvo-flow/templates/navReports/export-reports', function(require) {

return Ember.Handlebars.compile("<section class=\"fullWidth reportTools\" id=\"reportBlocks\">\n  {{#view FLOW.ExportReportsAppletView}}\n    <div class=\"surveySelect\">\n      <nav class=\"breadCrumb floats-in\">\n        {{#unless FLOW.projectControl.isLoading}}\n          {{view FLOW.SurveySelectionView}}\n        {{/unless}}\n        {{#if FLOW.selectedControl.selectedSurvey}}\n          {{view Ember.Select\n              contentBinding=\"FLOW.surveyControl.arrangedContent\"\n              selectionBinding=\"FLOW.selectedControl.selectedSurvey\"\n              optionLabelPath=\"content.code\"\n              optionValuePath=\"content.keyId\"\n              prompt=\"\"\n              promptBinding=\"Ember.STRINGS._select_form\" id=\"monitorSelection\"}}\n        {{/if}}\n      </nav>\n    </div>\n    <script>\n      function openExportOptions(evt, exportName) {\n        var i, options, trigger;\n        options = document.getElementsByClassName(\"options\");\n        for (i = 0; i < options.length; i++) {\n          options[i].style.display = \"none\";\n        }\n        trigger = document.getElementsByClassName(\"trigger\");\n        for (i = 0; i < trigger.length; i++) {\n          trigger[i].className = trigger[i].className.replace(\" active\", \"\");\n        }\n        document.getElementById(exportName).style.display = \"block\";\n        evt.currentTarget.className += \" active\";\n       }\n    </script> \n    <section class=\"exportContainer\">\n      <ul class=\"exportSelect\">\n        <li class=\"dataCleanExp trigger\" onclick=\"openExportOptions(event, 'dataCleanExp_options')\">\n          <h2>{{t _data_cleaning_export}}</h2>\n          <h6>{{t _importable_back_to_akvo_flow}}</h6>\n          <p class=\"expDescr\">{{t _combines_options}}</p>\n          <div id=\"dataCleanExp_options\" class=\"options\">\n              <div class=\"dataCollectedDate\">\n                <p>{{t _collection_period}}</p>\n                <label class=\"collectedFrom\">\n                  {{view FLOW.DateField minDate=false valueBinding=\"FLOW.dateControl.fromDate\" elementId=\"from_date02\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._collected_from\" size=30}}\n                </label>\n                <label class=\"collectedTo\">\n                  {{view FLOW.DateField minDate=false valueBinding=\"FLOW.dateControl.toDate\" elementId=\"to_date02\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._to\" size=30}}\n                </label>\n            </div>\n            <a {{action showDataCleaningReport target=\"this\"}} class=\"button trigger2\">{{t _download}}</a>\n          </div>\n        </li>\n        <li class=\"dataAnalyseExp trigger\" onclick=\"openExportOptions(event, 'dataAnalyseExp_options')\">\n          <h2>{{t _data_analysis_export}}</h2>\n          <h6>{{t _not_importable_back}}</h6>\n          <p class=\"expDescr\">{{t _replaces_question}}</p>\n            <div id=\"dataAnalyseExp_options\" class=\"options\">\n              <div class=\"dataCollectedDate\">\n                <p>{{t _collection_period}}</p>\n                <label class=\"collectedFrom\">\n                   {{view FLOW.DateField minDate=false valueBinding=\"FLOW.dateControl.fromDate\" elementId=\"from_date\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._collected_from\" size=30}}\n                </label>\n                <label class=\"collectedTo\">\n                  {{view FLOW.DateField minDate=false valueBinding=\"FLOW.dateControl.toDate\" elementId=\"to_date\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._to\" size=30}}\n                </label>\n              </div>\n              <a {{action showDataAnalysisReport target=\"this\"}} class=\"button trigger2\">{{t _download}}</a>\n            </div>\n          </li>\n          <li class=\"compReportExp trigger\"  onclick=\"openExportOptions(event, 'compReportExp_options')\">\n            <h2>{{t _comprehensive_report}}</h2>\n            <h6>{{t _not_importable_back}}</h6>\n            <p class=\"expDescr\">{{t _summarizes_responses}}</p>\n            <div id=\"compReportExp_options\" class=\"options\">\n              <a {{action showComprehensiveReport target=\"this\"}} class=\"button trigger2\">{{t _download}}</a>\n            </div>\n          </li>\n          <li class=\"geoShapeDataExp trigger\"  onclick=\"openExportOptions(event, 'geoShapeDataExp_options')\">\n            <h2>{{t _geoshape_data}}</h2>\n            <p class=\"expDescr\">{{t _geojson}}</p>\n              <div class=\"geoshapeSelect\">\n              {{#if FLOW.selectedControl.selectedSurvey}}\n                {{view Ember.Select\n                    contentBinding=\"FLOW.questionControl.geoshapeContent\"\n                    selectionBinding=\"FLOW.selectedControl.selectedQuestion\"\n                    optionLabelPath=\"content.text\"\n                    optionValuePath=\"content.keyId\"\n                    prompt=\"\"\n                    promptBinding=\"Ember.STRINGS._select_question\"}}\n              {{/if}}\n            </div>\n            <div id=\"geoShapeDataExp_options\" class=\"options\">\n              <a {{action showGeoshapeReport target=\"this\"}} class=\"button trigger2\">{{t _download}}</a>\n          </div>\n        </li>\n        <li class=\"surveyFormExp trigger\"  onclick=\"openExportOptions(event, 'surveyFormExp_options')\">\n          <h2>{{t _survey_form}}</h2>\n          <p class=\"expDescr\">{{t _printable}}</p>\n          <div id=\"surveyFormExp_options\" class=\"options\">\n            <a {{action showSurveyForm target=\"this\"}} class=\"button trigger2\">{{t _download}}</a>\n              {{#if view.showSurveyFormApplet }}\n                {{view FLOW.surveyFormApplet}}\n              {{/if}}\n        </div>\n      </li>\n    </ul>\n  </section>\n{{/view}}\n</section>");

});

loader.register('akvo-flow/templates/navReports/nav-reports', function(require) {

return Ember.Handlebars.compile("<section class=\"topBar\">\n    <div id=\"tabs\">\n        <nav class=\"tabNav floats-in\">\n        \t{{view FLOW.ReportsSubnavView controllerBinding=\"controller.controllers.reportsSubnavController\"}}\n        </nav>    \n    </div>\n</section>    \n<section class=\"reportsSection floats-in belowHeader\" id=\"main\" role=\"main\">\n    {{outlet}}\n</section>\n");

});

loader.register('akvo-flow/templates/navReports/reports-subnav', function(require) {

return Ember.Handlebars.compile("<ul>\n    {{#view view.NavItemView item=\"exportReports\" }}\n    \t<a {{action doExportReports}}>{{t _export_reports}}</a>\n    {{/view}} \n    {{#view view.NavItemView item=\"chartReports\" }}\n      <a {{action doChartReports}}>{{t _charts}}</a>\n    {{/view}} \n</ul>\n");

});

loader.register('akvo-flow/templates/navSurveys/edit-questions', function(require) {

return Ember.Handlebars.compile("<!-- Beginning Question group set  -->\n<section id=\"questionSet\">\n  <section class=\"aQuestionSet mainContent\" id=\"setIndex-01\">\n    <!-- zeroItem indicates that this is the item before the first question group -->\n    {{#view FLOW.QuestionGroupItemView zeroItem=true}}\n      {{#if view.showQuestionGroupModifyButtons}}\n        <!-- insert, move and copy buttons -->\n        {{#if view.oneSelectedForMove}}\n          <nav class=\"moveMenu groupActionMenu\">\n            <ul>\n              <li><a {{action \"doQGroupMoveHere\" target=\"this\"}} class=\"smallBtn\">{{t _move_group_here}}</a></li>\n              <li><a {{action \"doQGroupMoveCancel\" target=\"this\"}} class=\"\">{{t _cancel}}</a></li>\n            </ul>\n          </nav>\n        {{else}}\n          {{#if view.oneSelectedForCopy}}\n            <nav class=\"copyMenu groupActionMenu\">\n              <ul>\n                <li><a {{action \"doQGroupCopyHere\" target=\"this\"}} class=\"smallBtn\">{{t _paste_group_here}}</a></li>\n                <li><a {{action \"doQGroupCopyCancel\" target=\"this\"}} class=\"\">{{t _cancel}}</a></li>\n              </ul>\n            </nav>\n          {{else}}\n            <nav class=\"insertMenu groupActionMenu\">\n              <ul>\n                <li><a {{action \"doInsertQuestionGroup\" target=\"this\"}} class=\"\">{{t _insert_group_here}}</a></li>\n              </ul>\n            </nav>\n          {{/if}}\n        {{/if}}\n      {{/if}}\n      <!-- end insert, move and copy buttons for zero item-->\n    {{/view}}\n\n    <!-- start list of question groups -->\n    {{#each questionGroup in FLOW.questionGroupControl.arrangedContent}}\n      {{#view FLOW.QuestionGroupItemView contentBinding=\"questionGroup\"}}\n        <div class=\"questionGroupBlock\">\n          <header><span class=\"qtnGroupHead\">{{t _group}} {{view.content.order}}</span>\n\n          {{#if view.amCopying}} <div class=\"copyingSpinner\">{{t _copying}} </div>\n          {{/if}}\n\n            <div class=\"qtnGroupTitle\">\n            {{#if view.showQGroupNameEditField}}\n              {{view Ember.TextField valueBinding=\"view.content.code\" size=45}}\n            {{else}}\n              <h1 class=\"qtnGroupTitle\"><a {{action \"toggleVisibility\" target=\"this\"}}>{{view.content.code}}</a></h1>\n            {{/if}}\n            </div>\n            {{#if view.amVisible}}\n              <label class=\"labelcheckbox\">{{view Ember.Checkbox checkedBinding=\"view.content.repeatable\" disabledBinding=\"view.disableQuestionGroupEditing\"}}{{t _repeatable_question_group}}</label>\n              {{tooltip _repeatable_question_group_tooltip}}\n            {{/if}}\n            {{#if view.showSaveCancelButton}}\n              <div class=\"groupSave\">\n                <a {{action \"saveQuestionGroup\" target=\"this\"}} class=\"smallBtn\">{{t _save}}</a>\n              </div>\n            {{/if}}\n            <nav class=\"qtnGroupMenu\">\n              <ul>\n                {{#unless view.amCopying}}\n                  {{#if view.amVisible}}\n                    <li><a {{action \"toggleVisibility\" target=\"this\"}} class=\"showQuestionGroup shown\">{{t _hide_questions}} </a></li>\n                  {{else}}\n                    <li><a {{action \"toggleVisibility\" target=\"this\"}} class=\"showQuestionGroup\">{{t _show_questions}} </a></li>\n                  {{/if}}\n                  {{#if view.showQuestionGroupModifyButtons}}\n                     <li><a {{action \"doQGroupNameEdit\" target=\"this\"}} class=\"editQuestionGroup\">{{t _edit_group_name}}</a></li>\n                     <li><a {{action \"doQGroupMove\" target=\"this\"}} class=\"moveQuestionGroup\">{{t _move}}</a></li>\n                     <li><a {{action \"doQGroupCopy\" target=\"this\"}} class=\"copyQuestionGroup\">{{t _copy}}</a></li>\n                     <li><a {{action confirm FLOW.dialogControl.delQG target=\"FLOW.dialogControl\"}} class=\"deleteQuestionGroup\">{{t _delete}}</a></li>\n\n                  {{/if}}\n                {{/unless}}\n              </ul>\n            </nav>\n          </header>\n\n          <!-- if the question group is open, show all questions -->\n          {{#if view.amVisible}}\n            <div class=\"questionSetContent\">\n              {{view FLOW.QuestionView zeroItemQuestion=true}}\n                {{#each question in FLOW.questionControl}}\n                  {{view FLOW.QuestionView contentBinding=\"question\" zeroItemQuestion=false}}\n                {{/each}}\n            </div>\n          {{/if}}\n          <!-- end question group block -->\n        </div>\n\n        {{#if view.showQuestionGroupModifyButtons}}\n          <!-- insert, move and copy buttons -->\n          {{#if view.oneSelectedForMove}}\n            <nav class=\"moveMenu groupActionMenu\">\n              <ul>\n                <li><a {{action \"doQGroupMoveHere\" target=\"this\"}} class=\"smallBtn\">{{t _move_group_here}}</a></li>\n                <li><a {{action \"doQGroupMoveCancel\" target=\"this\"}} class=\"\">{{t _cancel}}</a></li>\n              </ul>\n            </nav>\n          {{else}}\n            {{#if view.oneSelectedForCopy}}\n              <nav class=\"copyMenu groupActionMenu\">\n                <ul>\n                  <li><a {{action \"doQGroupCopyHere\" target=\"this\"}} class=\"smallBtn\">{{t _paste_group_here}}</a></li>\n                  <li><a {{action \"doQGroupCopyCancel\" target=\"this\"}} class=\"\">{{t _cancel}}</a></li>\n                </ul>\n              </nav>\n            {{else}}\n              <nav class=\"insertMenu groupActionMenu\">\n                <ul>\n                  <li><a {{action \"doInsertQuestionGroup\" target=\"this\"}} class=\"\">{{t _insert_group_here}}</a></li>\n                </ul>\n              </nav>\n            {{/if}}\n          {{/if}}\n          <!-- end move and copy buttons -->\n        {{/if}}\n      {{/view}}\n    {{/each}}\n  </section>\n</section>\n<!-- End Question group Set  -->\n");

});

loader.register('akvo-flow/templates/navSurveys/form', function(require) {

return Ember.Handlebars.compile("{{#with FLOW.selectedControl.selectedSurvey as form}}\n<div id=\"form01\" class=\"aformContainer\">\n\t<nav class=\"newSurveyNav\">\n\t\t<ul>\n\t\t\t{{#if view.showFormPublishButton}}\n\t\t\t<li><a class=\"publishNewSurvey\" {{action \"publishSurvey\" target=\"FLOW.surveyControl\"}}>{{t _publish}}</a></li>\n\t\t\t{{/if}}\n\t\t\t<li><a class=\"previewNewSurvey\" {{action \"showPreview\" target=\"FLOW.surveyControl\"}}>{{t _preview}}</a></li>\n\t\t\t{{#if view.showFormDeleteButton}}\n\t\t\t\t<li><a class=\"deleteSurvey\" {{action confirm FLOW.dialogControl.delForm target=\"FLOW.dialogControl\"}}>{{t _delete}}</a></li>\n\t\t\t{{/if}}\n\t\t</ul>\n\t</nav>\n\t<ul class=\"formSummary\">\n\t\t<li>{{t _version}}<span class=\"formVersion\">{{form.version}}</span></li>\n\t\t<li><span class=\"upCase\">{{t _id}}</span><span class=\"formID\">{{form.keyId}}</span></li>\n\t\t<li>{{t _questions}}<span class=\"formQuestionCount\">{{FLOW.projectControl.questionCount}}</span></li>\n\t</ul>\n\t<section class=\"formDetails\">\n\t\t<h3>{{t _form_basics}}</h3>\n\t\t{{#if view.visibleFormBasics}}\n\t\t\t{{#unless view.isNewForm}}\n\t\t\t\t<a {{action \"toggleShowFormBasics\" target=\"this\"}} class=\"button\">{{t _collapse}}</a>\n\t\t\t{{/unless}}\n\t\t\t<form class=\"surveyDetailForm\" {{action 'saveProject' on='submit' target=\"FLOW.projectControl\"}}>\n\t\t\t\t<label>{{t _form_title}}</label>\n\t\t\t\t{{view Ember.TextField valueBinding=\"form.name\" disabledBinding=\"view.disableFormFields\"}}\n\t\t\t\t<nav class=\"newSurveyNav\">\n\t\t\t\t\t<ul class=\"manageStuff\">\n\t\t\t\t\t{{#if view.showFormTranslationsButton}}\n\t\t\t\t\t\t<li><a class=\"btnOutline\" {{action \"doManageTranslations\" target=\"this\"}}>{{t _manage_translations}}</a></li>\n\t\t\t\t\t\t<li><a class=\"btnOutline\" {{action \"doManageNotifications\" target=\"this\"}}>{{t _manage_notifications}}</a></li>\n\t\t\t\t\t{{/if}}\n\t\t\t\t\t</ul>\n\t\t\t\t</nav>\n\t\t\t</form>\n\t\t{{else}}\n\t\t\t<a {{action \"toggleShowFormBasics\" target=\"this\"}} class=\"button\">{{t _show}}</a>\n\t\t{{/if}}\n\t</section>\n\t<section class=\"surveyForm\">\n\t\t{{#if view.manageTranslations}}\n\t\t\t{{view FLOW.TranslationsView}}\n\t\t{{else}}\n\t\t\t{{#if view.manageNotifications}}\n\t\t\t\t{{view FLOW.NotificationsView}}\n\t\t\t{{else}}\n\t\t\t\t{{view FLOW.EditQuestionsView}}\n\t\t\t{{/if}}\n\t\t{{/if}}\n\t</section>\n</div>\n{{/with}}\n");

});

loader.register('akvo-flow/templates/navSurveys/manage-notifications', function(require) {

return Ember.Handlebars.compile("\n<div class=\"manageNotificationsBlock\">\n    <section id=\"manageNotifications\" class=\"mainContent\">\n        <div class=\"innerContent\">\n            <div id=\"notifications\">\n                <h1>\n                    {{t _notifications}}\n                </h1>\n                <div class=\"Separator\">\n\n                    <form class=\"notificationAdd\">\n                        <fieldset>\n                            <label for=\"emailNotification\">{{t _email}}  {{#if view.destinationEmpty}} - {{t _please_provide_an_email_address}} {{/if}}</label>\n                            {{view Ember.TextField valueBinding=\"view.notificationDestination\" id=\"emailNotification\" size=150 }}\n                        </fieldset>\n                        <fieldset>\n                            <label for=\"eventNotification\">{{t _event}}  {{#if view.typeEmpty}} - {{t _please_make_a_choice}}{{/if}}</label>\n                            {{view Ember.Select\n                            contentBinding=\"FLOW.notificationEventControl.content\"\n                            optionLabelPath=\"content.label\"\n                            optionValuePath=\"content.value\"\n                            selectionBinding=\"view.notificationType\"\n                            id=\"eventNotification\"\n                            prompt=\"\"\n                            promptBinding=\"Ember.STRINGS._select_event\"}}\n                        </fieldset>\n                        <fieldset>\n                            <label for=\"typeNotification\">{{t _option}}  {{#if view.optionEmpty}} - {{t _please_make_a_choice}} {{/if}}</label>\n                            {{view Ember.Select\n                            contentBinding=\"FLOW.notificationOptionControl.content\"\n                            optionLabelPath=\"content.label\"\n                            optionValuePath=\"content.value\"\n                            selectionBinding=\"view.notificationOption\"\n                            id=\"typeNotification\"\n                            prompt=\"\"\n                            promptBinding=\"Ember.STRINGS._select_option\"}}\n                        </fieldset>\n                        <fieldset>\n                            <label for=\"dateNotification\">{{t _expires}} {{#if view.dateEmpty}} - {{t _please_select_a_date}} {{/if}}</label> {{view FLOW.DateField2 valueBinding=\"view.expiryDate\" id=\"dateNotification\" size=30}}\n                        </fieldset>\n                        <fieldset class=\"addNotifiBtn\">\n                            <nav>\n                                <ul>\n                                    <li><a {{action \"addNotification\" target=\"this\"}} class=\"smallBtn\">{{t _add}}</a></li>\n                                    <li><a {{action \"cancelNotification\" target=\"this\"}} >{{t _cancel}}</a></li>\n                                </ul>\n                            </nav>\n                        </fieldset>\n                    </form>\n                    <table class=\"notificationTable dataTable\">\n                        <!-- DEVICES TABLE--><!-- TABLE HEADER-->\n                        <thead>\n                            <tr>\n                                <th>{{t _email}}</th>\n                                <th>{{t _type}}</th>\n                                <th>{{t _option}}</th>\n                                <th>{{t _expires}}</th>\n                                <th></th>\n                            </tr>\n                        </thead><!-- TABLE BODY: MAIN CONTENT-->\n                        <tbody>\n                          {{#each notification in FLOW.notificationControl}}\n                            <tr>\n                                <td class=\"\">{{notification.notificationDestination}}</td>\n                                <td class=\"\">{{notification.notificationType}}</td>\n                                <td class=\"\">{{notification.notificationOption}}</td>\n                                <td class=\"\">{{#with notification}}{{date3 expiryDate}}{{/with}}</td>\n                                <td class=\"action\">\n                                    <a {{action \"removeNotification\" notification target=\"this\"}}>{{t _remove}}</a>\n                                </td>\n                            </tr>\n                            {{/each}}\n                        </tbody><!-- TABLE FOOTER-->\n                        <tfoot>\n                            <tr>\n                                <td colspan=\"7\">\n                                    <small>This is the footer.</small>\n                                </td>\n                            </tr>\n                        </tfoot>\n                    </table>\n                </div>\n            </div>\n            <nav class>\n                <ul>\n                    <li>\n                        <a {{action \"closeNotifications\" target=\"this\"}} class=\"smallBtn\">{{t _close_notifications}}</a>\n                    </li>\n                </ul>\n            </nav>\n        </div>\n    </section>\n</div>\n");

});

loader.register('akvo-flow/templates/navSurveys/manage-translations', function(require) {

return Ember.Handlebars.compile("\n<div class=\"manageTranslationsBlock\">\n    <section id=\"manageTranslations\" class=\"\">\n       <section class=\"floats-in surveyTranslation\">\n    <h1>{{t _form_translation}}</h1>\n    <table id=\"languageOption\">\n      <!-- TABLE HEADER-->\n      <thead>\n        <tr>\n          <th>{{t _default_language}}</th>\n          <th>{{t _existing_translations}}</th>\n          <th>{{t _add_new_translation}}</th>\n        </tr>\n      </thead>\n\n      <!-- TABLE BODY: MAIN CONTENT-->\n      <tbody>\n        <tr>\n          <td class=\"basicLang\">{{FLOW.translationControl.defaultLang}}</td>\n          <td class=\"existingLang\"><ul>\n             {{#each trans in FLOW.translationControl.translations}}\n              <li><a {{action \"switchTranslation\" trans target=\"FLOW.translationControl\"}}>{{trans.label}}</a></li>\n             {{/each}}\n            </ul></td>\n          <td class=\"newLang\">\n             {{view Ember.Select\n         contentBinding=\"FLOW.translationControl.isoLangs\"\n         selectionBinding=\"FLOW.translationControl.selectedLanguage\"\n         optionLabelPath=\"content.labelLong\"\n         optionValuePath=\"content.value\"\n         prompt=\"\"\n         promptBinding=\"Ember.STRINGS._select_language\"}}\n            <a {{action \"addTranslation\" target=\"FLOW.translationControl\"}} class=\"smallBtn\">{{t _add}}</a>\n            {{#if FLOW.translationControl.newSelected}}\n              <a {{action \"cancelAddTranslation\" target=\"FLOW.translationControl\"}} class=\"smallBtn\">{{t _cancel}}</a>\n           {{/if}}</td>\n        </tr>\n      {{#unless FLOW.translationControl.newSelected}}\n        <tr>\n          <td class=\"formTitle\" colspan=3>{{t _select_an_existing_translation_}}</td>\n        </tr>\n        {{/unless}}\n        {{#if FLOW.translationControl.newSelected}}\n          <tr>\n            <td class=\"formTitle\" colspan=3><strong>{{t _please_click_add_}} {{FLOW.translationControl.selectedLanguage.labelShort}}</strong></td>\n          </tr>\n        {{/if}}\n      </tbody>\n    </table>\n\n\n  <div class=\"twoColumns floats-in\">\n  <table class=\"surveyTranslate surveyDetails\">\n    <!-- TABLE HEADER-->\n    <thead>\n      <tr>\n        <th class=\"defaultLanguage\"><h3>{{t _form_details}}</h3></th>\n        <th class=\"targetLanguage\"><h3>{{t _form_details_in}} <em>{{FLOW.translationControl.currentTranslationName}}</em></h3></th>\n      </tr>\n    </thead>\n</table>\n</div>\n\n\n  <div class=\"twoColumns floats-in\">\n    <table class=\"surveyTranslate surveyDetails\">\n    <tbody>\n      {{#each item in FLOW.translationControl.itemArray}}\n        {{#if item.isSurvey}}\n          <tr>\n            <td class=\"defaultLanguage\"><label>{{t _form_title}}</label>\n            <p>{{item.surveyText}}</p></td>\n          <td class=\"targetLanguage\"><label for=\"surveyName\">{{t _form_title}}</label>\n            {{view Ember.TextField valueBinding=\"item.surveyTextTrans\" disabledBinding=\"FLOW.translationControl.blockInteraction\" size=40 }}</td>\n        </tr>\n        {{/if}}\n      {{/each}}\n      </tbody>\n    </table>\n    </div> <!--  end twocolumns -->\n    <header style=\"heigh:40px; padding: 10px 0 10px 0\">\n      <nav class=\"qtnGroupMenu\">\n        <ul>\n          <li style=\"text-align:center;margin:0 10px 0 0;\"><a  {{action \"saveTranslations\" target=\"FLOW.translationControl\"}} class=\"button\">{{t _save_translations}}</a> </li>\n        </ul>\n      </nav>\n    </header>\n\n    <section class=\"aQuestionSet mainContent\">\n     \t{{#each questionGroup in FLOW.questionGroupControl}}\n    \t{{#view FLOW.QuestionGroupItemTranslationView contentBinding=\"questionGroup\"}}\n    \t <div class=\"questionGroupBlock\">\n    \t   <header> <span class=\"qtnGroupHead\">{{t _group}} {{view.content.order}}</span>\n    \t     <h1 class=\"qtnGroupTitle\"><a {{action \"toggleVisibility\" target=\"this\"}}>{{view.content.code}}</a></h1>\n    \t      <nav class=\"qtnGroupMenu\">\n                <ul>\n                  {{#if view.amVisible}}\n                    <li><a {{action \"toggleVisibility\" target=\"this\"}} class=\"showQuestionGroup shown\" id=\"\">{{t _hide_questions}} </a></li>\n                    <li><a  {{action \"saveTranslations\" target=\"FLOW.translationControl\"}} class=\"button smallButton\">{{t _save_translations}}</a> </li>\n                  {{else}}\n                    <li><a {{action \"toggleVisibility\" target=\"this\"}} class=\"showQuestionGroup\" id=\"\">{{t _show_questions}} </a></li>\n                  {{/if}}\n                </ul>\n              </nav>\n    \t  </header>\n    \t  {{#if view.amVisible}}\n    \t <div class=\"twoColumns floats-in\">\n    <table class=\"surveyTranslate surveyDetails\">\n    <tbody>\n      {{#each item in FLOW.translationControl.itemArray}}\n          {{#if item.isQG}}\n            <tr>\n              <td class=\"defaultLanguage\"> <span class=\"qtnGroupHead\">{{t _group}} {{item.displayOrder}}</span>\n              <h1 class=\"qtnGroupTitle\">{{item.qgText}}</h1></td>\n              <td class=\"targetLanguage\"> <span class=\"qtnGroupHead\">{{t _group}} {{item.displayOrder}}</span>\n              {{view Ember.TextField valueBinding=\"item.qgTextTrans\" disabledBinding=\"FLOW.translationControl.blockInteraction\" size=40 }}</td>\n            </tr>\n             <tr>\n                <td class=\"defaultLanguage\"><h3>{{t _questions}}:</h3></td>\n                <td class=\"targetLanguage\"><h3>{{t _questions}}:</h3></td>\n            </tr>\n          {{else}}\n            {{#if item.isQ}}\n            <tr>\n              <td class=\"defaultLanguage\"><div class=\"questionNbr\"><span>{{item.displayOrder}}</span>{{item.qText}}</div>\n                {{#if item.hasTooltip}}\n                  <div class=\"qHelpTooltip\">\n                    <label class=\"qTooltip\">{{t _question_help_tooltip_optional}}</label>\n                    <p>{{item.qTipText}}</p>\n                  </div>\n                  {{/if}}\n                </td>\n              <td class=\"targetLanguage\"><div class=\"questionNbr\"><span>{{item.displayOrder}}</span>\n              {{view Ember.TextField valueBinding=\"item.qTextTrans\" disabledBinding=\"FLOW.translationControl.blockInteraction\" size=40 }}\n              {{#if item.hasTooltip}}\n                </div>\n                <div class=\"qHelpTooltip\">\n              <label class=\"qTooltip\">{{t _question_help_tooltip_optional}}</label>\n              {{view Ember.TextField valueBinding=\"item.qTipTextTrans\" disabledBinding=\"FLOW.translationControl.blockInteraction\" size=40 }}\n            </div>\n            {{/if}}\n          </td>\n            </tr>\n            {{else}}\n              {{#if item.isQO}}\n                <tr class=\"indented\">\n                  <td  class=\"defaultLanguage\"><div class=\"questionNbr\"><span>{{item.displayOrder}}</span>{{item.qoText}}</div></td>\n                  <td  class=\"targetLanguage\"><div class=\"questionNbr\"><span>{{item.displayOrder}}</span>\n                  {{view Ember.TextField valueBinding=\"item.qoTextTrans\" disabledBinding=\"FLOW.translationControl.blockInteraction\" size=40 }}\n                  </div></td>\n                </tr>\n\n              {{/if}}\n            {{/if}}\n        {{/if}}\n      {{/each}}\n      </tbody>\n    </table>\n    </div> <!--  end twocolumns -->\n    <header style=\"heigh:40px; padding: 0 0 10px 0\">\n     <h1 class=\"qtnGroupTitle\">&nbsp;</h1>\n      <nav class=\"qtnGroupMenu\">\n        <ul>\n          <li><a {{action \"toggleVisibility\" target=\"this\"}} class=\"showQuestionGroup shown\" id=\"\">{{t _hide_questions}} </a></li>\n          <li><a  {{action \"saveTranslations\" target=\"FLOW.translationControl\"}} class=\"standardBtn\">{{t _save_translations}}</a> </li>\n        </ul>\n      </nav>\n    </header>\n    \t{{/if}}\n    \t \t </div>\n    \t{{/view}}\n    {{/each}}\n    </section>\n\n    <nav class=\"bottomPg\">\n      <ul>\n         <li><a  {{action \"saveTranslationsAndClose\" target=\"this\"}} class=\"button\">{{t _save_and_close}}</a> </li>\n        <li><a {{action \"closeTranslations\" target=\"this\"}}>{{t _close_without_saving}}</a> </li>\n      </ul>\n    </nav>\n    </section>\n      </section>\n</div>\n");

});

loader.register('akvo-flow/templates/navSurveys/nav-surveys-edit', function(require) {

return Ember.Handlebars.compile("<!-- we are within navSurveysEditView -->\n{{#view FLOW.SurveySidebarView}}\n<section class=\"leftSidebar surveyDetailSideBar\" id=\"newSurveyInfo\">\n    <h2>{{t _edit_survey}}</h2>\n\n    <form>\n        <label for=\"newSurveyName\"><span>{{t _title}}<span class=\"isRequired\">({{t _required}})</span>:</span>\n            {{view Ember.TextField valueBinding=\"view.surveyTitle\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._type_the_name_of_your_survey\" size=30}}</label>\n        <label for=\"newSurveyDesc\"><span>{{t _description}}:</span>\n            {{view Ember.TextArea valueBinding=\"view.surveyDescription\" placeholder=\"\" placeholderBinding=\"Ember.STRINGS._type_a_description_of_your_survey\" size=30 rows=\"3\" }}</label>\n        <!--  <label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.requireApproval\"}}{{t _require_approval}} </label> -->\n        <dl class=\"floats-in\">\n            <dt>{{t _version_number}}:</dt>\n            <dd><span id=\"versionNbr\">{{FLOW.selectedControl.selectedSurvey.version}}</span>{{tooltip _version_numbers_helps}}</dd>\n            <dt>{{t _id_number}}:</dt>\n            <dd><span id=\"surveyIdNbr\">{{FLOW.selectedControl.selectedSurvey.keyId}}</span>\n            </dd>\n        </dl>\n        <label for=\"surveyType\"><span>{{t _type}}<span class=\"isRequired\">({{t _required}})</span>:</span>\n            {{view Ember.Select contentBinding=\"FLOW.surveyPointTypeControl.content\" optionLabelPath=\"content.label\" optionValuePath=\"content.value\" selectionBinding=\"view.surveyPointType\" prompt=\"\" promptBinding=\"Ember.STRINGS._select_survey_type\"}}\n        </label>\n\n        <label for=\"surveyLanguage\"><span>{{t _master_language}}:</span>{{tooltip _master_lang_no_change}} {{view Ember.Select contentBinding=\"FLOW.translationControl.isoLangs\" selectionBinding=\"view.language\" optionLabelPath=\"content.labelShort\" optionValuePath=\"content.value\" prompt=\"\" promptBinding=\"Ember.STRINGS._select_language\"}}\n        </label>\n    </form>\n\n    <ul class=\"manageStuff floats-in\">\n        <li>\n            <a {{action \"doManageTranslations\" target=\"this\" }} class=\"btnOutline\"><span>+</span>{{t _manage_translations}}</a>\n        </li>\n        <li>\n            <a {{action \"doManageNotifications\" target=\"this\" }} class=\"btnOutline\"><span>+</span>{{t _manage_notifications}}</a>\n        </li>\n    </ul>\n  \n    <ul class=\"newSurveyInfoUl\" id=\"\">\n        <li>\n            <h3>{{t _survey_summary}}</h3>\n        </li>\n        <li>{{t _number_of_question_groups}}: <span id=\"numberQuestionSet\">{{view.numberQuestionGroups}}</span> \n        </li>\n        <li>{{t _number_of_questions}}: <span id=\"numberQuestion\">{{view.numberQuestions}}</span> \n        </li>\n        <li>{{t _status}}: {{#if view.isPublished}} <span class=\"surveyPublished\">\n      {{t _published}}</span> {{else}} <span class=\"surveyNotPublished\">\n      {{t _not_published}}</span> {{/if}}\n        </li>\n    </ul>\n    <nav class=\"newSurveyNav\">\n        <ul>\n            <li><a {{action doSaveSurvey target=\"this\" }} class=\"saveNewSurvey\">{{t _save}}</a>\n            </li>\n            <li><a {{action doPreviewSurvey target=\"this\" }} class=\"previewNewSurvey\">{{t _preview}}</a>\n            </li>\n            <li><a {{action doPublishSurvey target=\"this\" }} class=\"publishNewSurvey\">{{t _publish}}</a>\n            </li>\n        </ul>\n    </nav>\n\n</section>\n<section class=\"mainRight\" id=\"surveyCreator\">\n    <section class=\"topBar\">\n        <nav class=\"menuTopbar\">\n            <ul class=\"\">\n                <li><a {{action \"doSurveysMain\" target=\"this\" }} class=\"stepBack\">{{t _go_back_to_survey_overview}}</a>\n                </li>\n            </ul>\n        </nav>\n    </section>\n\n    {{outlet}}\n</section>\n{{/view }}\n<!-- end of FLOW.SurveySideBarView -->\n");

});

loader.register('akvo-flow/templates/navSurveys/nav-surveys-main', function(require) {

return Ember.Handlebars.compile("  {{#view FLOW.ProjectMainView}}\n    <section class=\"topBar\">\n      <nav class=\"breadCrumb\">\n        <ul>\n          <li>\n            <a class=\"homeRoot\" {{action \"selectRootProject\" target=\"FLOW.projectControl\"}}>{{t _home}}</a>\n          </li>\n          {{#each sg in FLOW.projectControl.breadCrumbs}}\n            <li>\n              <a {{action \"selectProject\" sg target=\"FLOW.projectControl\"}}>{{sg.code}}</a>\n            </li>\n          {{/each}}\n        </ul>\n      </nav>\n      {{#if view.projectListView}}\n        {{#if FLOW.projectControl.moveTarget}}\n          <nav class=\"menuTopbar actionHighLighted\">\n            <ul>\n              <li><p>{{t _moving}} <span class=\"itemMoved\">{{FLOW.projectControl.moveTarget.code}}</span> {{FLOW.projectControl.moveTargetType}}</p></li>\n              {{#if view.disableAddSurveyButtonInRoot}}\n              <li><a class=\"moveAction button noChanges\">{{t _move_here}}</a></li>\n              {{else}}\n              <li><a class=\"moveAction button\" {{action \"endMoveProject\" target=\"FLOW.projectControl\"}}>{{t _move_here}}</a></li>\n              {{/if}}\n              <li><a class=\"redCancel  btnOutline\" {{action \"cancelMoveProject\" target=\"FLOW.projectControl\"}}>{{t _cancel}}</a></li>\n            </ul>\n          </nav>\n        {{else}}\n          {{#if FLOW.projectControl.copyTarget}}\n            <nav class=\"menuTopbar actionHighLighted\">\n              <ul>\n                <li><p>{{t _copying}} <span class=\"itemMoved\">{{FLOW.projectControl.copyTarget.code}}</span></p></li>\n                {{#if view.disableAddSurveyButtonInRoot}}\n                <li><a class=\"moveAction button noChanges\">{{t _copy_here}}</a></li>\n                {{else}}\n                <li><a class=\"moveAction button\" {{action \"endCopyProject\" target=\"FLOW.projectControl\"}}>{{t _copy_here}}</a></li>\n                {{/if}}\n                <li><a class=\"redCancel btnOutline\" {{action \"cancelCopyProject\" target=\"FLOW.projectControl\"}}>{{t _cancel}}</a></li>\n              </ul>\n            </nav>\n          {{else}}\n            <nav class=\"menuTopbar\">\n              <ul>\n              {{#if view.disableAddFolderButton }}\n                <li><a class=\"addFolder noChanges\">{{t _add_folder}}</a></li>\n              {{else}}\n                <li><a class=\"addFolder\" {{action \"createProjectFolder\" target=\"FLOW.projectControl\"}}>{{t _add_folder}}</a></li>\n              {{/if}}\n              {{#if view.disableAddSurveyButton }}\n                <li><a class=\"addSurvey noChanges\">{{t _create_new_survey}}</a></li>\n              {{else}}\n                {{#if view.disableAddSurveyButtonInRoot}}\n                  <li><a class=\"addSurvey noChanges tooltip\" title=\"{{t _survey_only_in_folder}}\" >{{t _create_new_survey}}</a></li>\n                {{else}}\n                  <li><a class=\"addSurvey\" {{action \"createProject\" target=\"FLOW.projectControl\"}}>{{t _create_new_survey}}</a></li>\n                {{/if}}\n              {{/if}}\n              </ul>\n            </nav>\n          {{/if}}\n        {{/if}}\n      {{else}}\n        <nav class=\"menuTopbar\">\n          <ul>\n            {{#if view.hasUnsavedChanges}}\n              <li><a class=\"saveProject\" {{action 'saveProject' target=\"FLOW.projectControl\"}}>{{t _save}}</a></li>\n            {{else}}\n              <li><a class=\"saveProject noChanges\">{{t _save}}</a></li>\n            {{/if}}\n          </ul>\n        </nav>\n      {{/if}}\n    </section>\n  {{#if view.projectListView}}\n    {{view FLOW.ProjectListView}}\n  {{/if}}\n  {{#if view.projectView}}\n    {{view FLOW.ProjectView}}\n  {{/if}}\n{{/view}}\n");

});

loader.register('akvo-flow/templates/navSurveys/nav-surveys', function(require) {

return Ember.Handlebars.compile("{{outlet}}\n\n<div {{bindAttr class=\"FLOW.previewControl.showPreviewPopup:display :overlay\"}}>\n  <div class=\"blanketWide\"></div>\n  <div class=\"dialogWrap\">\n     <div class=\"confirmDialog dialogWide\">\n    <!-- the dialog contents -->\n    {{#if FLOW.previewControl.showPreviewPopup}}\n      {{view FLOW.PreviewView}}\n    {{/if}}\n  </div>\n  </div>\n</div>\n");

});

loader.register('akvo-flow/templates/navSurveys/preview-view', function(require) {

return Ember.Handlebars.compile("<div class=\"fixedMenu\"><a {{action \"closePreviewPopup\" target=\"this\"}} class=\"ok closeDialog\">{{t _close_window}}</a>\n<h2>{{t _survey_preview}}</h2></div>\n<div class=\"surveyPreviewWrap\">\n{{#each QG in FLOW.questionGroupControl}}\n <div class=\"questionGroupBlock\">\n   <header> <span class=\"qtnGroupHead\">{{t _group}} {{QG.order}}</span>\n     <h3 class=\"qtnGroupTitle\">{{QG.code}}</h3>\n     <div class=\"innerContent\"> \n       {{#view FLOW.PreviewQuestionGroupView contentBinding=\"QG\"}}\n         {{#each Q in view.QGcontent}}\n           {{#view FLOW.PreviewQuestionView contentBinding=\"Q\"}}\n             {{#if view.isVisible}}\n\t\t\t <div class=\"previewQuestion\"> \n                <h1 class=\"questionNbr\"><span>{{Q.order}}} </span>{{Q.text}}</h1>\n                    {{#if view.isOptionType}}\n                       {{#each view.optionsList}}\n                       <!-- FIXME this should be checkbuttons if Allow Multiple is true -->\n                          {{view Em.RadioButton title=this.value option=this.value group=\"options\" valueBinding=\"view.optionChoice\"}} \n                        {{/each}}\n                    {{else}}\n                      {{#if view.isNumberType}}\n                        {{view Ember.TextField valueBinding=\"view.answer\" size=10 }}\n                      {{else}}\n                        {{#if view.isTextType}}\n                          {{view Ember.TextField valueBinding=\"view.answer\" size=10 }}\n                        {{else}}\n                          {{#if view.isDateType}} \n                             {{view FLOW.DateField2 valueBinding=\"view.answer\" size=30}} \n                          {{else}}\n                            {{#if view.isGeoType}}\n                              <h3>{{t _the_gps_of_the_device_is_used_here}}</h3>\n                              <h4>{{t _latitude}}:</h4> {{view Ember.TextField valueBinding=\"view.latitude\" size=10 }}\n                              <h4>{{t _longitude}}:</h4> {{view Ember.TextField valueBinding=\"view.longitude\" size=10 }}\n                            {{else}}\n                              {{#if view.isBarcodeType}}\n                                <h3>{{t _the_barcode_app_on_the_device_is_used_here}}</h3>\n                                {{view Ember.TextField valueBinding=\"view.answer\" size=10 }}\n                              {{else}}\n                                {{#if view.isPhotoType}}\n                                  <h3>{{t _the_camera_of_the_device_is_used_here}}</h3>\n                                {{else}}\n                                  {{#if view.isVideoType}}\n                                    <h3>{{t _the_video_camera_of_the_device_is_used_here}}</h3>\n                                    {{else}}\n                                     {{#if view.isGeoshapeType}}\n                                      <h3>{{t _the_geographic_shape_editor_is_used_here}}</h3>\n                                     {{else}}\n                                     \t{{#if view.isCascadeType}}\n                                     \t\t{{#each item in view.levelNameList}}\n                                     \t\t\t<h4>{{item}}</h4> {{view Ember.TextField valueBinding=\"view.answer\" size=10 }}\n                                     \t\t{{/each}}\n                                     \t{{/if}}\n                                     {{/if}}\n                                  {{/if}}\n                                {{/if}}\n                              {{/if}}\n                            {{/if}}\n                          {{/if}}\n                        {{/if}}\n                      {{/if}}\n                    {{/if}}    \n                </div>\n              {{/if}}\n            {{/view}}\n          {{/each}}\n       {{/view}}\n     </div>\n </div>    \n{{/each}}\n</div>");

});

loader.register('akvo-flow/templates/navSurveys/project-list', function(require) {

return Ember.Handlebars.compile("\n\n<div class=\"floats-in\">\n  <div id=\"pageWrap\" class=\"widthConstraint belowHeader\">\n    <section id=\"allSurvey\" class=\"surveysList\">\n      {{#view FLOW.ProjectList}}\n        {{#each sg in FLOW.projectControl.currentFolders}}\n            {{#view FLOW.ProjectItemView contentBinding=\"sg\"}}\n              {{#if view.isFolder}}\n                {{#if view.folderEdit}}\n                  <a {{action \"toggleEditFolderName\" sg target=\"this\"}} class=\"editingFolderName\">\n                    {{t _edit_folder_name}}\n                  </a>\n                  {{view FLOW.FolderEditView valueBinding=\"sg.code\" contentBinding=\"sg\"}}\n                {{else}}\n                  {{#if view.showSurveyEditButton}}\n                  <a {{action \"toggleEditFolderName\" sg target=\"this\"}} class=\"editFolderName\">\n                    {{t _edit_folder_name}}\n                  </a>\n                  {{/if}}\n                  <a {{action \"selectProject\" sg target=\"FLOW.projectControl\"}}>\n                    <h2>{{sg.code}}</h2>\n                  </a>\n                {{/if}}\n                <nav>\n                  <ul>\n                    {{#unless view.hideFolderSurveyDeleteButton}}\n                      <li class=\"deleteSurvey\"><a {{action \"deleteProject\" sg target=\"FLOW.projectControl\"}}>{{t _delete}}</a></li>\n                    {{/unless}}\n                    {{#if view.showSurveyMoveButton}}\n                    <li class=\"moveSurvey\"><a {{action \"beginMoveProject\" sg target=\"FLOW.projectControl\"}}>{{t _move}}</a></li>\n                    {{/if}}\n                  </ul>\n               </nav>\n              {{else}}\n                <a {{action \"selectProject\" sg target=\"FLOW.projectControl\"}}>\n                  <h2>{{sg.code}}</h2>\n                </a>\n                <ul class=\"surveyInfo floats-in\">\n                  <li class=\"dateCreated\"><span>{{t _created}}</span><p>{{view.created}}</p></li>\n                  <li class=\"responseNumber\"><span>{{t _responses}}</span></li>\n                  <li class=\"dateModified\"><span>{{t _modified}}</span><p>{{view.modified}}</li>\n                  <li class=\"surveyType\"><span>{{t _type}}</span><p>\n                    {{#if view.isPrivate}}\n                      {{t _private}}\n                    {{else}}\n                      {{t _public}}\n                    {{/if}}\n                  </p></li>\n                  <li class=\"surveyLanguage\"><span>{{t _language}}</span><p>{{view.language}}</p></li>\n                </ul>\n                <nav>\n                  <ul>\n                    {{#if view.showSurveyEditButton}}\n                      <li class=\"editSurvey\"><a {{action \"selectProject\" sg target=\"FLOW.projectControl\"}}>{{t _edit}}</a></li>\n                    {{/if}}\n                    {{#if view.showSurveyMoveButton}}\n                      <li class=\"moveSurvey\"><a {{action \"beginMoveProject\" sg target=\"FLOW.projectControl\"}}>{{t _move}}</a></li>\n                    {{/if}}\n                    {{#unless view.hideFolderSurveyDeleteButton}}\n                      <li class=\"deleteSurvey\"><a {{action \"deleteProject\" sg target=\"FLOW.projectControl\"}}>{{t _delete}}</a></li>\n                    {{/unless}}\n                    {{#if view.showSurveyCopyButton}}\n                      <li class=\"copySurvey\" {{action \"beginCopyProject\" sg target=\"FLOW.projectControl\"}}><a>{{t _copy}}</a></li>\n                    {{/if}}\n                  </ul>\n                </nav>\n              {{/if}}\n            {{/view}}\n        {{/each}}\n      {{/view}}\n    </section>\n  </div>\n</div>\n");

});

loader.register('akvo-flow/templates/navSurveys/project', function(require) {

return Ember.Handlebars.compile("\n{{#view FLOW.Project}}\n  <div class=\"floats-in\">\n    <div id=\"pageWrap\" class=\"widthConstraint belowHeader\">\n      <section id=\"main\" class=\"projectSection floats-in middleSection\" role=\"main\">\n        <section id=\"\" class=\"projectDetailsPanel\">\n          <h2>{{FLOW.projectControl.currentProject.name}}</h2>\n          <ul class=\"projectSummary\">\n            <li>{{t _forms}}<span class=\"projectForm\">{{FLOW.projectControl.formCount}}</span>\n            </li>\n            <!-- Should we display the ID as we do with forms? -->\n            <li class=\"hidden\">{{t _id}}\n              <span class=\"projectForm\">\n                {{FLOW.projectControl.currentProject.keyId}}\n              </span>\n            </li>\n            <li>{{t _monitoring}}\n              <span class=\"projectMonitoring\">\n                {{#if FLOW.projectControl.currentProject.monitoringGroup}}{{t _enabled}}{{else}}{{t _not_enabled}}{{/if}}\n              </span>\n            </li>\n          </ul>\n          <section class=\"projectDetails\">\n            <h3>{{t _survey_basics}}</h3>\n            {{#unless view.isNewProject}}\n              <a {{action \"toggleShowProjectDetails\" target=\"this\"}} class=\"button\">\n                {{#if view.visibleProjectBasics}}\n                  {{t _collapse}}\n                {{else}}\n                  {{t _show}}\n                {{/if}}\n              </a>\n            {{/unless}}\n            {{#if view.visibleProjectBasics}}\n              <form class=\"projectDetailForm\" {{action 'saveProject' on='submit' target=\"FLOW.projectControl\"}}>\n                <label>{{t _survey_title}}</label>{{view Ember.TextField id=\"projectTitle\" valueBinding=\"FLOW.projectControl.currentProject.name\" disabledBinding=\"view.disableFolderSurveyInputField\"}}\n\n                <ul class=\"projectSelect floats-in\">\n                  <li>\n                    <label>{{t _privacy_type}}:</label>\n                    {{view Ember.Select\n                      contentBinding=\"FLOW.privacyLevelControl.content\"\n                      selectionBinding=\"FLOW.projectControl.currentProject.privacyLevel\"\n                      disabledBinding=\"view.disableFolderSurveyInputField\"}}\n                  </li>\n                  <li>\n                    <label>{{t _language}}:</label>\n                    {{view Ember.Select\n                      contentBinding=\"FLOW.languageControl.content\"\n                      selectionBinding=\"view.selectedLanguage\"\n                      optionLabelPath=\"content.label\"\n                      optionValuePath=\"content.value\"\n                      disabledBinding=\"view.disableFolderSurveyInputField\"}}\n                  </li>\n                </ul>\n                {{#if FLOW.projectControl.hasForms}}\n                      <label for=\"enableMonitoring\" class=\"labelcheckbox\">\n                        {{view Ember.Checkbox checkedBinding=\"FLOW.projectControl.currentProject.monitoringGroup\" id=\"enableMonitoring\" disabledBinding=\"view.disableFolderSurveyInputField\"}}{{t _enable_monitoring_features}}\n                      </label>\n                      {{#if FLOW.projectControl.currentProject.monitoringGroup}}\n\n                      <p class=\"monitoringHint\">{{t _choose_the_registration_form}}: {{tooltip _choose_the_registration_form_tooltip}}</p>\n                      {{view Ember.Select\n                        contentBinding=\"FLOW.surveyControl.arrangedContent\"\n                        selectionBinding=\"view.selectedRegistrationForm\"\n                        optionLabelPath=\"content.code\"\n                        optionValuePath=\"content.keyId\"\n                        disabledBinding=\"view.disableFolderSurveyInputField\"}}\n\n\n\n\n                    {{#if view.showDataApproval}}\n                        <label for=\"enableDataApproval\" class=\"labelcheckbox\">\n                            {{view Ember.Checkbox checkedBinding=\"FLOW.projectControl.currentProject.requireDataApproval\" id=\"enableDataApproval\" disabledBinding=\"view.disableFolderSurveyInputField\"}} {{t _enable_data_approval}} {{tooltip _enable_data_approval_tooltip}}\n                        </label>\n                        {{#if view.showDataApprovalList}}\n                          {{view Ember.Select\n                            contentBinding=\"FLOW.router.approvalGroupListController.arrangedContent\"\n                            optionLabelPath=\"content.name\"\n                            optionValuePath=\"content.keyId\"\n                            selectionBinding=\"FLOW.projectControl.dataApprovalGroup\"\n                            disabledBinding=\"view.disableFolderSurveyInputField\"\n                            promptBinding=\"Ember.STRINGS._choose_data_approval_group\"}}\n\n                            {{#if view.showDataApprovalDetails}}\n                              <div class=\"hideShow\">\n                                <a {{action toggleShowDataApprovalDetails target=\"view\"}}>{{t _hide_approval}}</a>\n                              </div>\n                                {{#view FLOW.SurveyApprovalView controllerBinding=\"FLOW.router.approvalGroupController\"}}\n                                  <div class=\"approvalDetail\">\n                                    <h2>{{name}}</h2>\n                                    <p>{{#if ordered}} {{t _ordered_approval}} {{else}} {{t _unordered_approval}} {{/if}}</p>\n                                    <ul class=\"approvalSteps\">\n                                    {{#each step in FLOW.router.approvalStepsController}}\n                                        {{#view FLOW.SurveyApprovalStepView stepBinding=\"step\"}}\n                                            <li><h4>{{view.step.title}}</h4> <a {{action toggleShowResponsibleUsers target=\"view\"}}>{{t _responsible_users}}</a></li>\n                                            {{#if view.showResponsibleUsers}}\n                                            <div>\n                                                <ul class=\"responsibleUsers\">\n                                                    {{#each user in FLOW.router.userListController}}\n                                                        {{#view FLOW.ApprovalResponsibleUserView\n                                                                    userBinding=\"user\"\n                                                                    stepBinding=\"view.step\"}}\n                                                            <li>\n                                                            {{view Ember.Checkbox\n                                                                    checkedBinding=\"view.isResponsibleUser\"}}\n                                                            {{view.user.userName}}\n                                                            </li>\n                                                        {{/view}}\n                                                    {{/each}}\n                                                </ul>\n                                            </div>\n                                            {{/if}}\n                                        {{/view}}\n                                    {{/each}}\n                                    </ul>\n                                  </div>\n                                {{/view}}\n                              {{else}}\n                              <div class=\"hideShow\">\n                                <a {{action toggleShowDataApprovalDetails target=\"view\"}}>{{t _show_approval}}</a>\n                              </div>\n                            {{/if}}\n                        {{/if}}\n                    {{/if}}\n                  {{/if}}\n                {{/if}}\n              </form>\n            {{/if}}\n          </section>\n\n          <section class=\"noFormsContainer\">\n            {{#unless FLOW.projectControl.hasForms}}\n              <ul>\n                  <li class=\"formList\"><p class=\"noForms\">{{t _no_forms_in_this_survey}}</p></li>\n                  {{#if view.showAddNewFormButton}}\n                  <li><a class=\"addMenuAction aBtn addNewForm\" {{action \"createForm\" target=\"FLOW.surveyControl\"}}>{{t _add_new_form}}</a></li>\n                  {{/if}}\n              </ul>\n            {{/unless}}\n          </section>\n\n          <section class=\"forms\">\n\n            {{#if FLOW.projectControl.hasForms}}\n              <div id=\"tabs\">\n                {{#if FLOW.projectControl.currentProject.monitoringGroup}}\n                {{#if view.showAddNewFormButton}}\n                  <nav class=\"menuTopbar\">\n                    <ul>\n                      <li>  <a {{action \"createForm\" target=\"FLOW.surveyControl\"}} class=\"button addFormBtn\" >{{t _add_new_form}}</a></li>\n                    </ul>\n                  </nav>                      \n                {{/if}}\n            {{/if}}\n                <nav class=\"tabNav floats-in\">\n                  <ul>\n                    {{#each form in FLOW.surveyControl}}\n                      {{#view FLOW.FormTabView contentBinding=\"form\"}}\n                        <a {{action \"selectForm\" form target=\"FLOW.surveyControl\"}}>{{form.name}}</a></li>\n                      {{/view}}\n                    {{/each}}\n\n                  </ul>\n                </nav>\n                <section class=\"formsContainer\">\n                  {{#if view.isPublished}}\n                    <div id=\"form01\" class=\"published\">\n                      {{#if FLOW.selectedControl.selectedSurvey}}\n                        <h3>{{FLOW.selectedControl.selectedSurvey.name}}</h3>\n                        {{view FLOW.FormView}}\n                      {{/if}}\n                    </div>\n                  {{else}}\n                    <div id=\"form01\">\n                      {{#if FLOW.selectedControl.selectedSurvey}}\n                        <h3>{{FLOW.selectedControl.selectedSurvey.name}}</h3>\n                        {{view FLOW.FormView}}\n                      {{/if}}\n                    </div>\n                  {{/if}}\n                </section>\n              </div>\n            {{/if}}\n          </section>\n        </section>\n      </section>\n    </div>\n  </div>\n{{/view}}\n");

});

loader.register('akvo-flow/templates/navSurveys/question-option', function(require) {

return Ember.Handlebars.compile("{{view Ember.TextField valueBinding=\"view.content.code\" placeholderBinding=\"Ember.STRINGS._code\" size=5}}\n{{view Ember.TextField valueBinding=\"view.content.text\" placeholderBinding=\"Ember.STRINGS._text_placeholder\" size=15}}\n<a {{action deleteOption target=\"FLOW.questionOptionsControl\"}}>{{t _delete_option}}</a>");

});

loader.register('akvo-flow/templates/navSurveys/question-view', function(require) {

return Ember.Handlebars.compile("{{#unless view.zeroItemQuestion}}\n\n<div class=\"innerContent\" id=\"innerContent_01\">\n  {{#if view.amOpenQuestion}}\n    <h1 class=\"questionNbr\"><span>{{view.content.order}}} </span>{{view.content.text}}</h1>\n    <label>{{t _question_text}}:\n    {{#if view.questionValidationFailure }}\n      <span style=\"color:red\">{{view.questionValidationFailureReason}}</span>\n      {{/if}}\n      {{view Ember.TextField valueBinding=\"view.text\" size=100 }}</label>\n    <label>{{t _question_help_tooltip}}: <span class=\"fadedText\">({{t _optional}})</span>\n    {{#if view.questionTooltipValidationFailure }}\n      <span style=\"color:red\">{{t _tooltip_over_500_chars_header}}</span>\n      {{/if}}\n      {{view Ember.TextField valueBinding=\"view.tip\" size=100 }} </label>\n    <label>\n      {{t _variable_name}}: <span class=\"fadedText\">({{t _optional}})</span> {{tooltip _variable_name_tooltip}}\n      {{#if view.variableNameValidationFailure }}\n      <span style=\"color:red\">{{view.variableNameValidationFailureReason}}</span>\n      {{/if}}\n      {{view Ember.TextField valueBinding=\"view.variableName\" size=100}}\n    </label>\n    <label class=\"labelcheckbox\">{{view Ember.Checkbox checkedBinding=\"view.mandatoryFlag\"}}{{t _mandatory}}</label>\n\n  <label class=\"selectinLabel\">\n    <span>\n      {{t _question_type}}:\n      {{#if view.amDateType}}\n        {{tooltip _question_type_date_tooltip}}\n      {{/if}}\n      {{#if view.amGeoshapeType}}\n        {{tooltip _question_type_geoshape_tooltip}}\n      {{/if}}\n    </span>\n    {{view Ember.Select\n      contentBinding=\"FLOW.questionTypeControl.content\"\n      optionLabelPath=\"content.label\"\n      optionValuePath=\"content.value\"\n      selectionBinding=\"view.type\" }}\n  </label>\n\n  {{#if view.showMetaConfig}}\n    {{#if view.showLocaleName}}\n      <label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.localeNameFlag\"}}{{t _use_in_record_display}} {{tooltip _use_in_record_display_tooltip}}</p></label>\n    {{else}}\n      {{#if view.amGeoType}}\n          <label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.localeLocationFlag\"}}{{t _use_as_record_location}} {{tooltip _use_as_record_location_tooltip}}</label>\n      {{/if}}\n    {{/if}}\n  {{/if}}\n\n    <!-- Question specific material -->\n{{#if view.hasExtraSettings}}\n<div class=\"questionOption floats-in\">\n    {{#if view.amOptionType}}\n      \n      <h1 class=\"answerNbr\">{{t _settings}}: </h1>\n      <ul>\n        <li><label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.allowMultipleFlag\"}}{{t _allow_multiple}} </label></li>\n        <li><label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.allowOtherFlag\"}}{{t _allow_other}} </label>\n        </li>\n      </ul>\n      <p class=\"optionTitle\"><strong>{{t _options}}:&nbsp;</strong></p>\n      <div class=\"optionListView\">\n    \t  {{view FLOW.OptionListView contentBinding=\"FLOW.questionOptionsControl\"}}\n    \t  <a {{action addOption target=\"FLOW.questionOptionsControl\"}} class=\"optionAdd\">{{t _add_option}}</a>\n      </div>\n    {{/if}}\n    {{#if view.amFreeTextType}}\n    <h1 class=\"answerNbr\">{{t _settings}}:</h1>\n    <label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.requireDoubleEntry\"}}{{t _require_double_entry}} {{tooltip _require_double_entry_tooltip}}</label>\n    {{/if}}\n    {{#if view.amBarcodeType}}\n    <h1 class=\"answerNbr\">{{t _settings}}: </h1>\n      <ul>\n        <li><label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.allowMultipleFlag\"}}{{t _enable_bulk_barcode_scan}} </label></li>\n        <li><label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.geoLocked\"}}{{t _disable_manual_geo_edit}} </label></li>\n      </ul>\n    {{/if}}\n    {{#if view.amNumberType}}\n    <h1 class=\"answerNbr\">{{t _settings}}: </h1>\n      <ul>\n        <li><label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.allowSign\"}}{{t _allow_sign}} </label></li>\n        <li><label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.allowDecimal\"}}{{t _allow_decimal_point}} </label></li>\n        <li><label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.requireDoubleEntry\"}}{{t _require_double_entry}} {{tooltip _require_double_entry_tooltip}}</label></li>\n      </ul>\n\n      <ul className=\"twoColumns\">\n        <li><label class=\"minValNumb\">{{t _min_val}}: {{view Ember.TextField valueBinding=\"view.minVal\" size=10 }}</label></li>\n        <li><label class=\"maxValNumb\">{{t _max_val}}: {{view Ember.TextField valueBinding=\"view.maxVal\" size=10 }}</label></li>\n      </ul>\n    {{/if}}\n     {{#if view.amGeoType}}\n    <h1 class=\"answerNbr\">{{t _settings}}: </h1>\n      <label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.geoLocked\"}}{{t _disable_manual_geo_edit}} </label>\n    {{/if}}\n\n    {{#if view.amTextType}}\n      {{#if FLOW.Env.showExternalSourcesFeature}}\n        <label class=\"labelcheckbox\">{{view Ember.Checkbox checkedBinding=\"view.allowExternalSources\"}}{{t _allow_external_sources}}</label>\n      {{/if}}\n    {{/if}}\n\n    {{#if view.amCascadeType}}\n        <h1 class=\"answerNbr\">{{t _settings}}: </h1>\n         <label class=\"selectinLabel dependencySelect\"> {{t _choose_cascade_question_resource}}: {{tooltip _choose_cascade_question_resource_tooltip}}\n        {{view Ember.Select\n        contentBinding=\"FLOW.cascadeResourceControl.published.arrangedContent\"\n        selectionBinding=\"FLOW.selectedControl.selectedCascadeResource\"\n        optionLabelPath=\"content.name\"\n        optionValuePath=\"content.keyId\"\n        prompt=\"\"\n        promptBinding=\"Ember.STRINGS._select_cascade\"}}</label>\n    {{/if}}\n    {{#if view.amCaddisflyType}}\n        <h1 class=\"answerNbr\">{{t _settings}}: </h1>\n         <label class=\"selectinLabel dependencySelect\">\n         {{#if view.showCaddisflyTests}}\n         {{t _choose_caddisfly_question_resource}}: {{tooltip _choose_caddisfly_question_resource_tooltip}}\n            {{view Ember.Select\n            contentBinding=\"FLOW.router.caddisflyResourceController\"\n            selectionBinding=\"FLOW.selectedControl.selectedCaddisflyResource\"\n            optionLabelPath=\"content.displayName\"\n            optionValuePath=\"content.keyId\"\n            prompt=\"\"\n            promptBinding=\"Ember.STRINGS._select_caddisfly_test\"}}\n        {{else}}\n        <div class=\"errorLoading\">{{t _failed_load_caddisfly_tests}}</div>\n        {{/if}}\n        </label>\n    {{/if}}\n    {{#if view.amGeoshapeType}}\n    <h1 class=\"answerNbr\">{{t _settings}}: </h1>\n      <ul>\n        <li><label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.allowPoints\"}}{{t _allow_points}} </label></li>\n        <li><label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.allowLine\"}}{{t _allow_line}} </label></li>\n        <li><label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.allowPolygon\"}}{{t _allow_polygon}} </label></li>\n        <li><label class=\"labelcheckbox\"> {{view Ember.Checkbox checkedBinding=\"view.geoLocked\"}}{{t _disable_manual_geo_edit}} </label></li>\n      </ul>\n    {{/if}}\n</div>\n{{/if}}\n<!-- End of question specific material -->\n<div class=\"dependencyBlock\">\n    <label class=\"labelcheckbox\">{{view Ember.Checkbox checkedBinding=\"view.dependentFlag\"}}{{t _dependent}}\n    </label>\n\n     {{#if view.dependentFlag}}\n    <label class=\"selectinLabel dependencySelect\"> {{t _dependent_question}}:\n        {{view Ember.Select\n        contentBinding=\"FLOW.questionControl.earlierOptionQuestions\"\n        selectionBinding=\"FLOW.selectedControl.dependentQuestion\"\n        optionLabelPath=\"content.text\"\n        optionValuePath=\"content.keyId\"\n        prompt=\"\"\n        promptBinding=\"Ember.STRINGS._select_question\"}}</label>\n    {{/if}}\n     {{#if view.dependentFlag}}\n    {{#if FLOW.selectedControl.dependentQuestion}}\n     <div class=\"qDependency\">{{t _answer_of_dependent_question}}:</div>\n      {{#each item in FLOW.optionListControl.content}}\n        <label>{{view Ember.Checkbox checkedBinding=\"item.isSelected\"}}{{item.value}}</label>\n      {{/each}}\n      {{/if}}\n  {{/if}}\n</div>\n    <!-- End question specific material -->\n    <nav>\n      <ul>\n        {{#if view.variableNameValidationFailure }}\n        <li><a class=\"button noChanges\" id=\"standardBtn_01\">{{t _save_question}}</a> </li>\n        {{else}}\n        {{#if view.questionValidationFailure }}\n        <li><a class=\"button noChanges\" id=\"standardBtn_01\">{{t _save_question}}</a> </li>\n        {{else}}\n        <li><a class=\"button\" id=\"standardBtn_01\" {{action \"doSaveEditQuestion\" target=\"this\"}}>{{t _save_question}}</a> </li>\n        {{/if}}\n        {{/if}}\n        <li><a class=\"cancel\" id=\"standardBtn_01\" {{action \"doCancelEditQuestion\" target=\"this\"}}>{{t _cancel}}</a> </li>\n      </ul>\n    </nav>\n  {{else}}\n    <!-- nav is only displayed if question is closed -->\n    <nav class=\"smallMenu\">\n      <ul>\n\t\t{{#if view.showQuestionModifyButtons}}\n        <li><a {{action confirm FLOW.dialogControl.delQ target=\"FLOW.dialogControl\"}} class=\"deleteQuestion\">{{t _delete}}</a> </li>\n        <li><a {{action \"doQuestionCopy\" target=\"this\"}} class=\"copyQuestion\" id=\"\">{{t _copy}}</a></li>\n        <li><a {{action \"doQuestionMove\" target=\"this\"}} class=\"moveQuestion\" id=\"\">{{t _move}}</a></li>\n        <li><a {{action \"doQuestionEdit\" target=\"this\"}} class=\"editQuestion\" id=\"\">{{t _edit}}</a></li>\n\t\t{{/if}}\n      </ul>\n    </nav>\n    <h1 class=\"questionNbr\">\n        {{#if view.showQuestionModifyButtons}}\n        <a {{action \"doQuestionEdit\" target=\"this\"}} class=\"textLink\"><span>{{view.content.order}} </span>{{view.content.text}}</a>\n        {{else}}\n        <span>{{view.content.order}} </span>{{view.content.text}}\n        {{/if}}\n    </h1>\n\n  {{/if}}\n  </div>\n\n{{/unless}}\n  <div>\n\t{{#if view.showQuestionModifyButtons}}\n\t  {{#if view.oneSelectedForMove}}\n\t\t<nav class=\"moveQMenu questionActionMenu\">\n\t\t  <ul>\n\t\t\t<li><a {{action \"doQuestionMoveHere\" target=\"this\"}} class=\"button smallButton\">{{t _move_question_here}}</a></li>\n\t\t\t<li><a {{action \"doQuestionMoveCancel\" target=\"this\"}} class=\"\">{{t _cancel}}</a></li>\n\t\t  </ul>\n\t\t</nav>\n\t  {{else}}\n\t\t{{#if view.oneSelectedForCopy}}\n\t\t  <nav class=\"copyQMenu questionActionMenu\">\n\t\t\t<ul>\n\t\t\t  <li><a {{action \"doQuestionCopyHere\" target=\"this\"}} class=\"button smallButton\">{{t _paste_question_here}}</a></li>\n\t\t\t  <li><a {{action \"doQuestionCopyCancel\" target=\"this\"}} class=\"\">{{t _cancel}}</a></li>\n\t\t\t</ul>\n\t\t  </nav>\n\t\t {{else}}\n\t\t   <a {{action \"doInsertQuestion\" target=\"this\"}} class=\"addQuestion\">{{t _add_new_question}}</a>\n\t\t{{/if}}\n\t  {{/if}}\n\t{{/if}}\n  </div>\n");

});

loader.register('akvo-flow/templates/navUsers/nav-users', function(require) {

return Ember.Handlebars.compile("<iframe src=\"frames/users.html\"\n        id=\"users-iframe\"\n        width=\"100%\"  scrolling=\"no\" style=\"padding:60px 0 50px 0;\">\n</iframe>\n<script>\n    $(document).ready(function(){\n        $('#users-iframe').iFrameResize();\n    })\n</script>\n");

});

loader.register('akvo-flow/controllers/controllers-public', function(require) {
// ***********************************************//
//                 controllers
// ***********************************************//
// Define the main application controller. This is automatically picked up by
// the application and initialized.
require('akvo-flow/core-common');
require('akvo-flow/flowenv');
require('akvo-flow/controllers/general-controllers-common');
require('akvo-flow/controllers/maps-controllers-common');

FLOW.ApplicationController = Ember.Controller.extend({});

FLOW.NavMapsController = Ember.Controller.extend();

});

loader.register('akvo-flow/controllers/controllers', function(require) {
// ***********************************************//
//                 controllers
// ***********************************************//
// Define the main application controller. This is automatically picked up by
// the application and initialized.
require('akvo-flow/core-common');
require('akvo-flow/flowenv');
require('akvo-flow/controllers/languages');
require('akvo-flow/currentuser');
require('akvo-flow/controllers/permissions');
require('akvo-flow/controllers/general-controllers-common');
require('akvo-flow/controllers/survey-controllers');
require('akvo-flow/controllers/device-controllers');
require('akvo-flow/controllers/data-controllers');
require('akvo-flow/controllers/reports-controllers');
require('akvo-flow/controllers/maps-controllers-common');
require('akvo-flow/controllers/messages-controllers');
require('akvo-flow/controllers/user-controllers');
require('akvo-flow/controllers/survey-selection');

FLOW.ApplicationController = Ember.Controller.extend({});

FLOW.role = Ember.Object.create({
	SUPER_ADMIN: function () {
		return FLOW.currentUser && FLOW.currentUser.permissionList === 0;
	}.property(),

	ADMIN: function () {
		return FLOW.currentUser && FLOW.currentUser.permissionList <= 10;
	}.property(),

	USER: function () {
		return FLOW.currentUser && FLOW.currentUser.permissionList <= 20;
	}.property()
});

//require('akvo-flow/currentuser');

// Navigation controllers
FLOW.NavigationController = Em.Controller.extend({
  selected: null
});
FLOW.NavHomeController = Ember.Controller.extend();
FLOW.NavSurveysController = Ember.Controller.extend();
FLOW.NavSurveysEditController = Ember.Controller.extend();
FLOW.NavDevicesController = Ember.Controller.extend();
FLOW.DevicesSubnavController = Em.Controller.extend();
FLOW.DevicesTableHeaderController = Em.Controller.extend({
  selected: null
});

FLOW.NavDataController = Ember.Controller.extend();
FLOW.DatasubnavController = Em.Controller.extend();
FLOW.InspectDataController = Ember.ArrayController.extend();
FLOW.BulkUploadController = Ember.Controller.extend();
FLOW.DataCleaningController = Ember.Controller.extend();

FLOW.NavReportsController = Ember.Controller.extend();
FLOW.ReportsSubnavController = Em.Controller.extend();
FLOW.ExportReportsController = Ember.ArrayController.extend();
FLOW.ChartReportsController = Ember.Controller.extend();

FLOW.NavMapsController = Ember.Controller.extend();
FLOW.NavUsersController = Ember.Controller.extend();
FLOW.NavMessagesController = Ember.Controller.extend();
FLOW.NavAdminController = Ember.Controller.extend();

Ember.ENV.RAISE_ON_DEPRECATION = true;

});

loader.register('akvo-flow/controllers/data-controllers', function(require) {

function capitaliseFirstLetter(string) {
  if (Ember.empty(string)) return "";
  return string.charAt(0).toUpperCase() + string.slice(1);
}

FLOW.cascadeResourceControl = Ember.ArrayController.create({
  content:null,
  published:null,
  statusUpdateTrigger:false,
  levelNames:null,
  displayLevelName1: null, displayLevelName2: null, displayLevelName3: null,
  displayLevelNum1: null, displayLevelNum2: null, displayLevelNum3: null,
  sortProperties: ['name'],
  sortAscending: true,

  populate: function() {
    this.set('content', FLOW.store.find(FLOW.CascadeResource));
    this.set('published', Ember.ArrayController.create({
      sortProperties: ['name'],
      sortAscending: true,
      content: FLOW.store.filter(FLOW.CascadeResource,function(item){
        return item.get('status') === 'PUBLISHED';
      })
    }));
  },

  setLevelNamesArray: function(){
    var i=1, levelNamesArray=[], numLevels;
    numLevels = FLOW.selectedControl.selectedCascadeResource.get('numLevels');

    // store the level names in an array
    FLOW.selectedControl.selectedCascadeResource.get('levelNames').forEach(function(item){
      if (i <= numLevels) {
        levelNamesArray.push(Ember.Object.create({
          levelName: item,
          level:i}));
        i++;
      }
    });
    this.set('levelNames',levelNamesArray);
  },

  setDisplayLevelNames: function(){
    var skip, names, numLevels;
    skip = FLOW.cascadeNodeControl.get('skip');
    names = this.get('levelNames');
    numLevels = FLOW.selectedControl.selectedCascadeResource.get('numLevels');
    this.set('displayLevelName1',names[skip].get('levelName'));
    if (numLevels > 1) {
      this.set('displayLevelName2',names[skip + 1].get('levelName'));
    } else {
      this.set('displayLevelName2',"");
    }
    if (numLevels > 2) {
      this.set('displayLevelName3',names[skip + 2].get('levelName'));
    } else {
      this.set('displayLevelName3',"");
    }
    this.set('displayLevelNum1',skip + 1);
    this.set('displayLevelNum2',skip + 2);
    this.set('displayLevelNum3',skip + 3);
  },

  publish: function(cascadeResourceId){
    FLOW.store.findQuery(FLOW.Action, {
      action: 'publishCascade',
      cascadeResourceId: cascadeResourceId
    });
  },

  hasQuestions: function () {
    if (!FLOW.selectedControl.selectedCascadeResource || !FLOW.selectedControl.selectedCascadeResource.get('keyId')) {
      return;
    }
    FLOW.store.findQuery(FLOW.Question, {cascadeResourceId: FLOW.selectedControl.selectedCascadeResource.get('keyId')});
  }.observes('FLOW.selectedControl.selectedCascadeResource'),

  triggerStatusUpdate: function(){
    this.toggleProperty('statusUpdateTrigger');
  },

  currentStatus: function () {
    // hack to get translation keys, don't delete them
    // {{t _not_published}}
    // {{t _publishing}}
    // {{t _published}}
    var status;
    if (!FLOW.selectedControl.selectedCascadeResource) {
      return '';
    }
    status = ('_' + FLOW.selectedControl.selectedCascadeResource.get('status')).toLowerCase();
    return Ember.String.loc(status);
  }.property('FLOW.selectedControl.selectedCascadeResource','this.statusUpdateTrigger'),

  isPublished: function () {
    if (!FLOW.selectedControl.selectedCascadeResource) {
      return false;
    }
    return FLOW.selectedControl.selectedCascadeResource.get('status') === 'PUBLISHED';
  }.property('FLOW.selectedControl.selectedCascadeResource','this.statusUpdateTrigger')
});

FLOW.cascadeNodeControl = Ember.ArrayController.create({
  content:null,
  level1:[], level2:[], level3:[], level4:[], level5:[], level6:[], level7:[],
  displayLevel1:[], displayLevel2:[], displayLevel3:[],
  parentNode:[],
  selectedNode:[],
  selectedNodeTrigger: true,
  skip: 0,

  emptyNodes: function(start){
    var i;
    for (i=start ; i < 6 ; i++){
      this.selectedNode[i]=null;
      this.set('level' + i,[]);
    }
  },

  toggleSelectedNodeTrigger:function (){
    this.toggleProperty('selectedNodeTrigger');
  },

  setDisplayLevels: function(){
    this.set('displayLevel1',this.get('level' + (this.get('skip') + 1)));
    this.set('displayLevel2',this.get('level' + (this.get('skip') + 2)));
    this.set('displayLevel3',this.get('level' + (this.get('skip') + 3)));
  },

  populate: function(cascadeResourceId, level, parentNodeId) {
    if (!cascadeResourceId) {
      return;
    }
    this.set('content',FLOW.store.findQuery(FLOW.CascadeNode, {
      cascadeResourceId: cascadeResourceId,
      parentNodeId: parentNodeId
    }));
    this.set('level' + level, FLOW.store.filter(FLOW.CascadeNode, function(item){
      return (item.get('parentNodeId') == parentNodeId && item.get('cascadeResourceId') == cascadeResourceId);
    }));
    this.parentNode[level] = parentNodeId;
    FLOW.cascadeNodeControl.setDisplayLevels();
  },

  addNode: function(cascadeResourceId, level, text, code) {
    var parentNodeId;
    if (level == 1) {
      parentNodeId = 0;
    } else {
      parentNodeId = this.get('parentNode')[level];
    }
    FLOW.store.createRecord(FLOW.CascadeNode, {
      "code": code,
      "name": capitaliseFirstLetter(text),
      "nodeId": null,
      "parentNodeId": parentNodeId,
      "cascadeResourceId": cascadeResourceId
    });
    if (FLOW.selectedControl.selectedCascadeResource.get('status') == 'PUBLISHED'){
      FLOW.selectedControl.selectedCascadeResource.set('status','NOT_PUBLISHED');
      FLOW.cascadeResourceControl.triggerStatusUpdate();
    }
    FLOW.store.commit();
    this.populate(cascadeResourceId, level, parentNodeId);
  },
});


FLOW.surveyInstanceControl = Ember.ArrayController.create({
  sortProperties: ['collectionDate'],
  sortAscending: false,
  selectedSurvey: null,
  content: null,
  sinceArray: [],
  pageNumber: 0,

  populate: function () {
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {}));
  },

  doInstanceQuery: function (surveyInstanceId, surveyId, deviceId, since, beginDate, endDate, submitterName, countryCode, level1, level2) {
    this.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {
      'surveyInstanceId': surveyInstanceId,
      'surveyId': surveyId,
      'deviceId': deviceId,
      'since': since,
      'beginDate': beginDate,
      'endDate': endDate,
      'submitterName': submitterName,
      'countryCode': countryCode,
      'level1': level1,
      'level2': level2
    }));
  },

  contentChanged: function() {
    var mutableContents = [];

    this.get('arrangedContent').forEach(function(item) {
      mutableContents.pushObject(item);
    });

    this.set('currentContents', mutableContents);
  }.observes('content', 'content.isLoaded'),

  removeInstance: function(instance) {
    this.get('currentContents').forEach(function(item, i, currentContents) {
      if (item.get('id') == instance.get('id')) {
        currentContents.removeAt(i, 1);
      }
    });
  },

  allAreSelected: function (key, value) {
    if (arguments.length === 2) {
      this.setEach('isSelected', value);
      return value;
    } else {
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }
  }.property('@each.isSelected'),

  atLeastOneSelected: function () {
    return this.filterProperty('isSelected', true).get('length');
  }.property('@each.isSelected'),

  // fired from tableColumnView.sort
  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});

FLOW.SurveyedLocaleController = Ember.ArrayController.extend({
  sortProperties: ['collectionDate'],
  sortAscending: false,
  selectedSurvey: null,
  sinceArray: [],
  pageNumber: 0,

  populate: function (criteria) {
    this.set('content', FLOW.store.find(FLOW.SurveyedLocale, criteria));
  },

  contentChanged: function() {
    var mutableContents = [];

    this.get('arrangedContent').forEach(function(item) {
      mutableContents.pushObject(item);
    });

    this.set('currentContents', mutableContents);
  }.observes('content', 'content.isLoaded'),

  removeLocale: function(locale) {
    this.get('currentContents').forEach(function(item, i, currentContents) {
      if (item.get('id') == locale.get('id')) {
        currentContents.removeAt(i, 1);
      }
    });
  },

  /* the current user is able to delete surveyed locales
    stored in the 'content' property of this controller */
  userCanDelete: function() {
    if(this.get('content') === null) {
      return false;
    }
    var surveyedLocale = this.get('content').get('firstObject'); // locale delete only allowed if enabled for the entire monitoring group
    if(surveyedLocale && surveyedLocale.get('surveyGroupId')) {
      return FLOW.surveyGroupControl.userCanDeleteData(surveyedLocale.get('surveyGroupId'));
    }
    return false; // prevents deletion incase no surveyId found
  }.property('content'),
});

FLOW.questionAnswerControl = Ember.ArrayController.create({
  content: null,

  // a computed property that returns a list containing *sub lists*
  // of responses to questions. Each sub list represents a single iteration
  // over a set of responses to questions in a specific question group, ordered
  // by question order. For repeat question groups two adjacent sub lists
  // represent two iterations of responses for that group
  contentByGroup: Ember.computed('content.isLoaded',
                                  'FLOW.questionContol.content.isLoaded',
                                  'FLOW.questionContol.content.isLoaded', function(key, value) {
    var content = Ember.get(this, 'content'), self = this;
    var questions = FLOW.questionControl.get('content');
    var questionGroups = FLOW.questionGroupControl.get('content');

    if (content && questions && questionGroups) {
		var surveyQuestions = FLOW.questionControl.get('content');
		var groups = FLOW.questionGroupControl.get('content');
		var allResponses = [];
		var groupResponses = [];
		var answersInGroup = [];
		var group, groupId, groupName, groupIteration, isRepeatable, questionsInGroup, questionGroupId, questionId;

		for (var i = 0; i < groups.get('length'); i++) {
			group = groups.objectAt(i);
			isRepeatable = group.get('repeatable');
			groupId = group.get('keyId');
			groupName = group.get('name');

			questionsInGroup = surveyQuestions.filterProperty('questionGroupId',groupId);

			for (var j = 0; j < questionsInGroup.get('length'); j++) {
				questionId = questionsInGroup[j].get('keyId').toString();
				answersInGroup = answersInGroup.concat(self.filterProperty('questionID', questionId));
			}

			if (isRepeatable) {
				groupIteration = 0;

				this.splitIterationAnswers(answersInGroup).forEach(function(iterationAnswers){
					if (iterationAnswers && iterationAnswers.length) {
						groupIteration++;
						iterationAnswers.groupName = groupName + " - " + groupIteration;
						groupResponses.push(iterationAnswers);
					}
				});
			} else {
				answersInGroup.groupName = groupName;
				groupResponses.push(answersInGroup);
			}
			allResponses.push(groupResponses);
			groupResponses = [];
			answersInGroup = [];
		}
		return Ember.A(allResponses);
	  }
	return content;
  }),

  /* take a list of question answer objects containing multiple iterations
   * and split into a list of sublists, each sublist containing a single iteration
   * answers group together */
  splitIterationAnswers: function(allAnswersInRepeatGroup){
	var allIterations = [];
	var iteration;

	for(var i = 0; i < allAnswersInRepeatGroup.length; i++) {
		iteration = allAnswersInRepeatGroup[i].get('iteration');
		if (!allIterations[iteration]) {
			allIterations[iteration] = [];
		}
		allIterations[iteration].push(allAnswersInRepeatGroup[i]);
	}
	return allIterations;
  },

  doQuestionAnswerQuery: function (surveyInstance) {
    var formId = surveyInstance.get('surveyId');
    var form = FLOW.surveyControl.filter(function (form) {
      return form.get('keyId') === formId;
    }).get(0);

    if (formId !== FLOW.selectedControl.selectedSurvey.get('keyId')) {
      FLOW.selectedControl.set('selectedSurvey', form);
    }

    this.set('content', FLOW.store.findQuery(FLOW.QuestionAnswer, {
      'surveyInstanceId': surveyInstance.get('keyId')
    }));
  },
});

FLOW.locationControl = Ember.ArrayController.create({
  selectedCountry: null,
  content:null,
  level1Content:null,
  level2Content:null,
  selectedLevel1: null,
  selectedLevel2: null,

  populateLevel1: function(){
    if (!Ember.none(this.get('selectedCountry')) && this.selectedCountry.get('iso').length > 0){
    this.set('level1Content',FLOW.store.findQuery(FLOW.SubCountry,{
      countryCode:this.selectedCountry.get('iso'),
      level:1,
      parentId:null
      }));
    }
  }.observes('this.selectedCountry'),

  populateLevel2: function(){
    if (!Ember.none(this.get('selectedLevel1')) && this.selectedLevel1.get('name').length > 0){
    this.set('level2Content',FLOW.store.findQuery(FLOW.SubCountry,{
      countryCode:this.selectedCountry.get('iso'),
      level:2,
      parentId:this.selectedLevel1.get('keyId')
      }));
    }
  }.observes('this.selectedLevel1')

});

FLOW.DataApprovalController = Ember.Controller.extend({});

FLOW.ApprovalGroupListController = Ember.ArrayController.extend({
    /* ---------------------
     * Controller Properties
     * ---------------------
     */
    sortProperties: ['name'],

    /* ---------------------
     * Controller Functions
     * ---------------------
     */

    /*
     * Load the list of approval groups
     */
    load: function () {
        this.set('content', FLOW.ApprovalGroup.find());
    },

    /*
     * Delete an approval group from the list
     */
    delete: function (group) {
        if(!group || !group.get('keyId')) {
            return;
        }

        var groups = this.content;
        var steps = FLOW.ApprovalStep.find({approvalGroupId: group.get('keyId')});
        var stepsController = FLOW.router.get('approvalStepsController');
        steps.on('didLoad', function () {
            steps.forEach(function (step) {
                step.deleteRecord();
            })
            group.deleteRecord();
            FLOW.store.commit();
        })
    },
});

FLOW.ApprovalGroupController = Ember.ObjectController.extend({

    /* ---------------------
     * Controller Properties
     * ---------------------
     */

    /*
     * Transform the `ordered` property on the ApprovalGroup model
     * to a string representation in order to bind successfully to
     * value attribute of the generated <option> entities
     */
    isOrderedApprovalGroup: function (key, value, previousValue) {
        var group = this.content;

        // setter
        if (group && arguments.length > 1) {
            group.set('ordered', value.trim() === "ordered")
        }

        // getter
        if(group && group.get('ordered')) {
            return "ordered";
        } else {
            return "unordered"
        }
    }.property('this.content'),




    /* ---------------------
     * Controller Functions
     * ---------------------
     */

    /*
     * Create a new approval group
     */
    add: function () {
        var group = FLOW.ApprovalGroup.createRecord({
            name: Ember.String.loc('_new_approval_group'),
            ordered: true,
        });

        this.set('content', group);
    },

    /*
     * Load the approval group by groupId
     */
    load: function (groupId) {
        if (groupId) {
            this.set('content', FLOW.ApprovalGroup.find(groupId));
        }
    },

    /*
     * Save an approval group and associated steps
     */
    save: function () {
        var validationError = this.validate();
        if(validationError) {
            return;
        }

        var group = this.content;
        if(group.get('name') !==  group.get('name').trim()) {
            group.set('name', group.get('name').trim());
        }

        FLOW.router.get('approvalStepsController').save(group);
    },

    /*
     * Validate approval group and associated steps
     */
    validate: function () {
        var stepsController = FLOW.router.get('approvalStepsController');
        var error = stepsController.validate();

        var group = this.content;
        if (!group.get('name') || !group.get('name').trim()) {
            error = Ember.String.loc('_blank_approval_group_name');
        }

        if(error) {
            FLOW.dialogControl.set('activeAction', 'ignore');
            FLOW.dialogControl.set('header', Ember.String.loc('_cannot_save'));
            FLOW.dialogControl.set('message', error);
            FLOW.dialogControl.set('showCANCEL', false);
            FLOW.dialogControl.set('showDialog', true);
        }

        return error;
    },

    /*
     * Cancel the editing of an approval group and its related
     * steps
     */
    cancel: function () {
        FLOW.store.get('defaultTransaction').rollback();
    },
});

FLOW.ApprovalStepsController = Ember.ArrayController.extend({

    /* ---------------------
     * Controller Properties
     * ---------------------
     */
    sortProperties: ['order'],

    /* ---------------------
     * Controller Functions
     * ---------------------
     */

    /*
     * Load approval steps for a given approval group
     */
    loadByGroupId: function (groupId) {
        var steps = Ember.A();
        if (groupId) {
            FLOW.ApprovalStep.find({approvalGroupId: groupId}).on('didLoad', function () {
                steps.addObjects(this);
            });
        }
        this.set('content',steps);
    },

    /*
     * Add an approval step for a given approval group
     */
    addApprovalStep: function () {
        var groupId = FLOW.router.get('approvalGroupController').get('content').get('keyId');
        if(!groupId) {
            FLOW.store.commit();
        }
        var steps = this.content;
        var lastStep = steps && steps.get('lastObject');

        // For cases where intermediate steps have been deleted during
        // the creation of an approval group, we do not update the order
        // of subsequent steps but rather ensure that the order of the
        // next step is based on the last step in the approval list. e.g
        // the order property for an approval list could be 0,2,4,6, where
        // steps with order 1,3 and 5 were deleted during group creation
        var newStep = Ember.Object.create({
            approvalGroupId: groupId,
            order: lastStep && lastStep.get('order') + 1 || 0,
            title: null,
        });
        steps.addObject(FLOW.store.createRecord(FLOW.ApprovalStep, newStep));
    },

    /*
     * Validate steps for erroneous input
     */
    validate: function () {
        var steps = this.content;
        var valid = true;

        steps.forEach(function (step) {
            var hasTitle = step.get('title') && step.get('title').trim();
            if(!hasTitle) {
                valid = false;
            }
        });

        var error;
        if (valid) {
            error = '';
        } else {
            error = Ember.String.loc('_blank_approval_step_title');
        }
        return error;
    },

    /*
     * Save approval steps
     */
    save: function(group) {
        var steps = this.content || [];
        steps.forEach(function (step, index) {
            if(step.get('code') && step.get('code').trim()) {
                step.set('code', step.get('code').trim());
            } else {
                step.set('code', null);
            }
            step.set('title', step.get('title').trim());

            if (!step.get('approvalGroupId') && group && group.get('keyId')) {
                step.set('approvalGroupId', group.get('keyId'));
            }
        });

        FLOW.store.commit();
    },

    /*
     * Delete an approval step
     */
    deleteApprovalStep: function (event) {
        var step = event.context;
        var steps = this.content;
        steps.removeObject(step);
        step.deleteRecord();
    },
});

FLOW.DataPointApprovalController = Ember.ArrayController.extend({

    content: Ember.A(),

    /**
     * add an approval element
     */
    add: function (dataPointApproval) {
        var dataPointApprovalList = this.content;
        var approval = FLOW.store.createRecord(FLOW.DataPointApproval, dataPointApproval);
        approval.on('didCreate', function () {
            dataPointApprovalList.addObject(approval);
        });

        FLOW.store.commit();
    },

    /**
     * Update an existing approval element
     */
    update: function (dataPointApproval) {
        var dataPointApprovalList = this.content;
        dataPointApproval.on('didUpdate', function () {
            dataPointApprovalList.addObject(dataPointApproval);
        });
        FLOW.store.commit();
    },

    /**
     * Load approval elements based on the surveyedLocaleId (data point id)
     */
    loadBySurveyedLocaleId: function (surveyedLocaleId) {
        var dataPointApprovalList = this.content;
        var approvals = FLOW.DataPointApproval.find({ surveyedLocaleId: surveyedLocaleId });
        approvals.on('didLoad', function () {
            dataPointApprovalList.addObjects(this);
        });
    },
});

});

loader.register('akvo-flow/controllers/device-controllers', function(require) {
FLOW.deviceGroupControl = Ember.ArrayController.create({
  content: null,
  contentNoUnassigned: null,

  filterDevices: function () {
    this.set('contentNoUnassigned', FLOW.store.filter(FLOW.DeviceGroup, function (item) {
      return (item.get('keyId') == 1) ? false : true;
    }));
  },

  populate: function () {
    var unassigned;

    // create a special record, which will to be saved to the datastore
    // to represent all devices unassigned to a device group.
    unassigned = FLOW.store.filter(FLOW.DeviceGroup, function (item) {
      return item.get('keyId') == 1;
    });
    if (unassigned.toArray().length === 0) {
      unassigned = FLOW.store.createRecord(FLOW.DeviceGroup, {
        code: Ember.String.loc('_devices_not_in_a_group'),
        keyId: 1
      });
      // prevent saving of this item to the backend
      unassigned.get('stateManager').send('becameClean');
    }
    this.set('content', FLOW.store.find(FLOW.DeviceGroup));
    this.filterDevices();
  }
});

FLOW.deviceControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  selected: null,
  content: null,

  populate: function () {
    this.set('content', FLOW.store.findQuery(FLOW.Device, {}));
    this.set('sortProperties', ['lastPositionDate']);
    this.set('sortAscending', false);
  },

  allAreSelected: function (key, value) {
    if (arguments.length === 2) {
      this.setEach('isSelected', value);
      return value;
    } else {
      return !this.get('isEmpty') && this.everyProperty('isSelected', true);
    }
  }.property('@each.isSelected'),

  atLeastOneSelected: function () {
    return this.filterProperty('isSelected', true).get('length');
  }.property('@each.isSelected'),

  // fired from tableColumnView.sort
  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
  }
});


FLOW.devicesInGroupControl = Ember.ArrayController.create({
  content: null,
  sortProperties: ['combinedName'],
  sortAscending: true,
  setDevicesInGroup: function () {
    var deviceGroupId;
    if (FLOW.selectedControl.get('selectedDeviceGroup') && FLOW.selectedControl.selectedDeviceGroup.get('keyId') !== null) {
      deviceGroupId = FLOW.selectedControl.selectedDeviceGroup.get('keyId');

      // 1 means all unassigned devices
      if (deviceGroupId == 1) {
        this.set('content', FLOW.store.filter(FLOW.Device, function (item) {
          return Ember.empty(item.get('deviceGroup'));
        }));
      } else {
        this.set('content', FLOW.store.filter(FLOW.Device, function (item) {
          return parseInt(item.get('deviceGroup'), 10) == deviceGroupId;
        }));
      }
    }
  }.observes('FLOW.selectedControl.selectedDeviceGroup')
});


FLOW.surveyAssignmentControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,

  populate: function () {
    this.set('content', FLOW.store.find(FLOW.SurveyAssignment));
    this.set('sortProperties', ['name']);
    this.set('sortAscending', true);
  },

  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
    this.set('selected', FLOW.tableColumnControl.get('selected'));
  }
});

});

loader.register('akvo-flow/controllers/general-controllers-common', function(require) {
FLOW.dashboardLanguageControl = Ember.Object.create({
  dashboardLanguage: FLOW.Env.locale,

  content: [{ label: "English (Default)", value: "en"},
            { label: "Español", value: "es" },
            { label: "Français", value: "fr" },
            { label: "Bahasa Indonesia", value: "id"},
            { label: "Português", value: "pt" },
            { label: "Tiếng Việt", value: "vi"}],

  languageChanged: function () {
    var localeUrl = '/ui-strings.js?locale=' + this.dashboardLanguage;
    $.ajax({
      url: localeUrl,
      complete: function () {
        location.reload(false);
      },
    });
  }.observes('dashboardLanguage'),
});
 
 
FLOW.selectedControl = Ember.Controller.create({
  selectedSurveyGroup: null,
  selectedSurvey: null,
  selectedSurveys: [],
  selectedSurveyAllQuestions: null,
  selectedSurveyAssignment: null,
  dependentQuestion: null,
  selectedQuestionGroup: null,
  selectedQuestion: null,
  selectedOption: null,
  selectedDevice: null,
  selectedDevices: [],
  selectedDevicesPreview: [],
  selectedSurveysPreview: [],
  selectedForMoveQuestionGroup: null,
  selectedForCopyQuestionGroup: null,
  selectedForMoveQuestion: null,
  selectedForCopyQuestion: null,
  selectedCreateNewGroup: false,
  selectedSurveyOPTIONQuestions: null,
  selectedCascadeResource:null,
  selectedCaddisflyResource:null,
  radioOptions: "",
  cascadeImportNumLevels: null,
  cascadeImportIncludeCodes: null,

  // OptionQuestions:function (){
  //   console.log('optionquestions 1');
  // }.observes('this.selectedSurveyOPTIONQuestions'),

  // when selected survey changes, deselect selected surveys and question groups
  deselectSurveyGroupChildren: function () {
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.selectedControl.set('selectedSurveyAllQuestions', null);
    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedQuestion', null);
  }.observes('this.selectedSurveyGroup'),

  deselectSurveyChildren: function () {
    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedQuestion', null);
  }.observes('this.selectedSurvey')
});


// used in user tab
FLOW.editControl = Ember.Controller.create({
  newPermissionLevel: null,
  newUserName: null,
  newEmailAddress: null,
  editPermissionLevel: null,
  editUserName: null,
  editEmailAddress: null,
  editUserId: null,
  editAttributeName: null,
  editAttributeGroup: null,
  editAttributeType: null,
  editAttributeId: null
});


FLOW.tableColumnControl = Ember.Object.create({
  sortProperties: null,
  sortAscending: true,
  selected: null,
  content: null
});


// set by restadapter sideLoad meta
FLOW.metaControl = Ember.Object.create({
  numSILoaded: null, // used by data tab nextPage method
  numSLLoaded: null, // used by monitored data tab nextPage method
  since: null,
  num: null,
  message: null,
  status: null,
  cursorType: null,
}),


// set by javacript datepickers in views.js
FLOW.dateControl = Ember.Object.create({
  // filled by javacript datepicker defined in views.js and by inspect-data.handlebars
  // binding. This makes sure we can both pick a date with the datepicker, and enter
  // a date manually
  fromDate: null,
  toDate: null
});


FLOW.savingMessageControl = Ember.Object.create({
  areSavingBool: false,
  areLoadingBool: false,
  numberLoading: 0,

  numLoadingChange: function (delta) {
	  this.set('numberLoading',this.get('numberLoading') + delta);
	  if (this.get('numberLoading') < 0){
		  this.set('numberLoading', 0);
	  }
	  if (this.get('numberLoading') > 0) {
		  this.set('areLoadingBool', true);
	  } else {
		  this.set('areLoadingBool', false);
	  }
  },

  checkSaving: function () {
    if (FLOW.store.defaultTransaction.buckets.inflight.list.get('length') > 0) {
      this.set('areSavingBool', true);
    } else {
      this.set('areSavingBool', false);
    }
  }
});

});

loader.register('akvo-flow/controllers/languages', function(require) {
FLOW.isoLanguagesDict = {
  "ab": {
    "name": "Abkhaz",
    "nativeName": "аҧсуа"
  },
  "aa": {
    "name": "Afar",
    "nativeName": "Afaraf"
  },
  "af": {
    "name": "Afrikaans",
    "nativeName": "Afrikaans"
  },
  "ak": {
    "name": "Akan",
    "nativeName": "Akan"
  },
  "sq": {
    "name": "Albanian",
    "nativeName": "Shqip"
  },
  "am": {
    "name": "Amharic",
    "nativeName": "አማርኛ"
  },
  "ar": {
    "name": "Arabic",
    "nativeName": "العربية"
  },
  "an": {
    "name": "Aragonese",
    "nativeName": "Aragonés"
  },
  "hy": {
    "name": "Armenian",
    "nativeName": "Հայերեն"
  },
  "as": {
    "name": "Assamese",
    "nativeName": "অসমীয়া"
  },
  "av": {
    "name": "Avaric",
    "nativeName": "авар мацӀ, магӀарул мацӀ"
  },
  "ae": {
    "name": "Avestan",
    "nativeName": "avesta"
  },
  "ay": {
    "name": "Aymara",
    "nativeName": "aymar aru"
  },
  "az": {
    "name": "Azerbaijani",
    "nativeName": "azərbaycan dili"
  },
  "bm": {
    "name": "Bambara",
    "nativeName": "bamanankan"
  },
  "ba": {
    "name": "Bashkir",
    "nativeName": "башҡорт теле"
  },
  "eu": {
    "name": "Basque",
    "nativeName": "euskara, euskera"
  },
  "be": {
    "name": "Belarusian",
    "nativeName": "Беларуская"
  },
  "bn": {
    "name": "Bengali",
    "nativeName": "বাংলা"
  },
  "bh": {
    "name": "Bihari",
    "nativeName": "भोजपुरी"
  },
  "bi": {
    "name": "Bislama",
    "nativeName": "Bislama"
  },
  "bs": {
    "name": "Bosnian",
    "nativeName": "bosanski jezik"
  },
  "br": {
    "name": "Breton",
    "nativeName": "brezhoneg"
  },
  "bg": {
    "name": "Bulgarian",
    "nativeName": "български език"
  },
  "my": {
    "name": "Burmese",
    "nativeName": "ဗမာစာ"
  },
  "ca": {
    "name": "Catalan",
    "nativeName": "Català"
  },
  "ch": {
    "name": "Chamorro",
    "nativeName": "Chamoru"
  },
  "ce": {
    "name": "Chechen",
    "nativeName": "нохчийн мотт"
  },
  "ny": {
    "name": "Chichewa",
    "nativeName": "chiCheŵa"
  },
  "zh": {
    "name": "Chinese",
    "nativeName": "中文 (Zhōngwén)"
  },
  "cv": {
    "name": "Chuvash",
    "nativeName": "чӑваш чӗлхи"
  },
  "kw": {
    "name": "Cornish",
    "nativeName": "Kernewek"
  },
  "co": {
    "name": "Corsican",
    "nativeName": "corsu, lingua corsa"
  },
  "cr": {
    "name": "Cree",
    "nativeName": "ᓀᐦᐃᔭᐍᐏᐣ"
  },
  "hr": {
    "name": "Croatian",
    "nativeName": "hrvatski"
  },
  "cs": {
    "name": "Czech",
    "nativeName": "česky, čeština"
  },
  "da": {
    "name": "Danish",
    "nativeName": "dansk"
  },
  "dv": {
    "name": "Divehi",
    "nativeName": "Dhivehi; Maldivian"
  },
  "nl": {
    "name": "Dutch",
    "nativeName": "Nederlands, Vlaams"
  },
  "en": {
    "name": "English",
    "nativeName": "English"
  },
  "eo": {
    "name": "Esperanto",
    "nativeName": "Esperanto"
  },
  "et": {
    "name": "Estonian",
    "nativeName": "eesti, eesti keel"
  },
  "ee": {
    "name": "Ewe",
    "nativeName": "Eʋegbe"
  },
  "fo": {
    "name": "Faroese",
    "nativeName": "føroyskt"
  },
  "fj": {
    "name": "Fijian",
    "nativeName": "vosa Vakaviti"
  },
  "fi": {
    "name": "Finnish",
    "nativeName": "suomi, suomen kieli"
  },
  "fr": {
    "name": "French",
    "nativeName": "français, langue française"
  },
  "ff": {
    "name": "Fula",
    "nativeName": "Fulfulde; Fulah; Pulaar; Pular"
  },
  "gl": {
    "name": "Galician",
    "nativeName": "Galego"
  },
  "ka": {
    "name": "Georgian",
    "nativeName": "ქართული"
  },
  "de": {
    "name": "German",
    "nativeName": "Deutsch"
  },
  "el": {
    "name": "Greek, Modern",
    "nativeName": "Ελληνικά"
  },
  "gn": {
    "name": "Guaraní",
    "nativeName": "Avañeẽ"
  },
  "gu": {
    "name": "Gujarati",
    "nativeName": "ગુજરાતી"
  },
  "ht": {
    "name": "Haitian",
    "nativeName": "Kreyòl ayisyen; Haitian Creole"
  },
  "ha": {
    "name": "Hausa",
    "nativeName": "Hausa, هَوُسَ"
  },
  "he": {
    "name": "Hebrew (modern)",
    "nativeName": "עברית"
  },
  "hz": {
    "name": "Herero",
    "nativeName": "Otjiherero"
  },
  "hi": {
    "name": "Hindi",
    "nativeName": "हिन्दी, हिंदी"
  },
  "ho": {
    "name": "Hiri Motu",
    "nativeName": "Hiri Motu"
  },
  "hu": {
    "name": "Hungarian",
    "nativeName": "Magyar"
  },
  "ia": {
    "name": "Interlingua",
    "nativeName": "Interlingua"
  },
  "id": {
    "name": "Indonesian",
    "nativeName": "Bahasa Indonesia"
  },
  "ie": {
    "name": "Interlingue",
    "nativeName": "Occidental;"
  },
  "ga": {
    "name": "Irish",
    "nativeName": "Gaeilge"
  },
  "ig": {
    "name": "Igbo",
    "nativeName": "Asụsụ Igbo"
  },
  "ik": {
    "name": "Inupiaq",
    "nativeName": "Iñupiaq, Iñupiatun"
  },
  "io": {
    "name": "Ido",
    "nativeName": "Ido"
  },
  "is": {
    "name": "Icelandic",
    "nativeName": "Íslenska"
  },
  "it": {
    "name": "Italian",
    "nativeName": "Italiano"
  },
  "iu": {
    "name": "Inuktitut",
    "nativeName": "ᐃᓄᒃᑎᑐᑦ"
  },
  "ja": {
    "name": "Japanese",
    "nativeName": "日本語 (にほんご／にっぽんご)"
  },
  "jv": {
    "name": "Javanese",
    "nativeName": "basa Jawa"
  },
  "kl": {
    "name": "Kalaallisut, Greenlandic",
    "nativeName": "kalaallisut"
  },
  "kn": {
    "name": "Kannada",
    "nativeName": "ಕನ್ನಡ"
  },
  "kr": {
    "name": "Kanuri",
    "nativeName": "Kanuri"
  },
  "ks": {
    "name": "Kashmiri",
    "nativeName": "कश्मीरी, كشميري‎"
  },
  "kk": {
    "name": "Kazakh",
    "nativeName": "Қазақ тілі"
  },
  "km": {
    "name": "Khmer",
    "nativeName": "ភាសាខ្មែរ"
  },
  "quc": {
    "name":"K'iche', Quiché",
    "nativeName": "K'iche'"
  },
  "ki": {
    "name": "Kikuyu, Gikuyu",
    "nativeName": "Gĩkũyũ"
  },
  "rw": {
    "name": "Kinyarwanda",
    "nativeName": "Ikinyarwanda"
  },
  "ky": {
    "name": "Kirghiz, Kyrgyz",
    "nativeName": "кыргыз тили"
  },
  "kv": {
    "name": "Komi",
    "nativeName": "коми кыв"
  },
  "kg": {
    "name": "Kongo",
    "nativeName": "KiKongo"
  },
  "ko": {
    "name": "Korean",
    "nativeName": "한국어 (韓國語), 조선말 (朝鮮語)"
  },
  "ku": {
    "name": "Kurdish",
    "nativeName": "Kurdî, كوردی‎"
  },
  "kj": {
    "name": "Kwanyama, Kuanyama",
    "nativeName": "Kuanyama"
  },
  "la": {
    "name": "Latin",
    "nativeName": "latine, lingua latina"
  },
  "lb": {
    "name": "Luxembourgish",
    "nativeName": "Lëtzebuergesch"
  },
  "lg": {
    "name": "Luganda",
    "nativeName": "Luganda"
  },
  "li": {
    "name": "Limburgish",
    "nativeName": "Limburgs"
  },
  "ln": {
    "name": "Lingala",
    "nativeName": "Lingála"
  },
  "lo": {
    "name": "Lao",
    "nativeName": "ພາສາລາວ"
  },
  "lt": {
    "name": "Lithuanian",
    "nativeName": "lietuvių kalba"
  },
  "lu": {
    "name": "Luba-Katanga",
    "nativeName": ""
  },
  "lv": {
    "name": "Latvian",
    "nativeName": "latviešu valoda"
  },
  "gv": {
    "name": "Manx",
    "nativeName": "Gaelg, Gailck"
  },
  "mk": {
    "name": "Macedonian",
    "nativeName": "македонски јазик"
  },
  "mg": {
    "name": "Malagasy",
    "nativeName": "Malagasy fiteny"
  },
  "ms": {
    "name": "Malay",
    "nativeName": "bahasa Melayu, بهاس ملايو‎"
  },
  "ml": {
    "name": "Malayalam",
    "nativeName": "മലയാളം"
  },
  "mt": {
    "name": "Maltese",
    "nativeName": "Malti"
  },
  "mi": {
    "name": "Māori",
    "nativeName": "te reo Māori"
  },
  "mr": {
    "name": "Marathi (Marāṭhī)",
    "nativeName": "मराठी"
  },
  "mh": {
    "name": "Marshallese",
    "nativeName": "Kajin M̧ajeļ"
  },
  "mn": {
    "name": "Mongolian",
    "nativeName": "монгол"
  },
  "na": {
    "name": "Nauru",
    "nativeName": "Ekakairũ Naoero"
  },
  "nv": {
    "name": "Navajo, Navaho",
    "nativeName": "Diné bizaad"
  },
  "nb": {
    "name": "Norwegian Bokmål",
    "nativeName": "Norsk bokmål"
  },
  "nd": {
    "name": "North Ndebele",
    "nativeName": "isiNdebele"
  },
  "ne": {
    "name": "Nepali",
    "nativeName": "नेपाली"
  },
  "ng": {
    "name": "Ndonga",
    "nativeName": "Owambo"
  },
  "nn": {
    "name": "Norwegian Nynorsk",
    "nativeName": "Norsk nynorsk"
  },
  "no": {
    "name": "Norwegian",
    "nativeName": "Norsk"
  },
  "ii": {
    "name": "Nuosu",
    "nativeName": "ꆈꌠ꒿ Nuosuhxop"
  },
  "nr": {
    "name": "South Ndebele",
    "nativeName": "isiNdebele"
  },
  "oc": {
    "name": "Occitan",
    "nativeName": "Occitan"
  },
  "oj": {
    "name": "Ojibwe, Ojibwa",
    "nativeName": "ᐊᓂᔑᓈᐯᒧᐎᓐ"
  },
  "om": {
    "name": "Oromo",
    "nativeName": "Afaan Oromoo"
  },
  "or": {
    "name": "Oriya",
    "nativeName": "ଓଡ଼ିଆ"
  },
  "os": {
    "name": "Ossetian, Ossetic",
    "nativeName": "ирон æвзаг"
  },
  "pa": {
    "name": "Panjabi, Punjabi",
    "nativeName": "ਪੰਜਾਬੀ, پنجابی‎"
  },
  "pi": {
    "name": "Pāli",
    "nativeName": "पाऴि"
  },
  "fa": {
    "name": "Persian",
    "nativeName": "فارسی"
  },
  "pis": {
    "name": "Pijin",
    "nativeName": "Pijin"
  },
  "pl": {
    "name": "Polish",
    "nativeName": "polski"
  },
  "ps": {
    "name": "Pashto, Pushto",
    "nativeName": "پښتو"
  },
  "pt": {
    "name": "Portuguese",
    "nativeName": "Português"
  },
  "qu": {
    "name": "Quechua",
    "nativeName": "Runa Simi, Kichwa"
  },
  "rm": {
    "name": "Romansh",
    "nativeName": "rumantsch grischun"
  },
  "rn": {
    "name": "Kirundi",
    "nativeName": "kiRundi"
  },
  "ro": {
    "name": "Romanian, Moldavian, Moldovan",
    "nativeName": "română"
  },
  "ru": {
    "name": "Russian",
    "nativeName": "русский язык"
  },
  "sa": {
    "name": "Sanskrit (Saṁskṛta)",
    "nativeName": "संस्कृतम्"
  },
  "sc": {
    "name": "Sardinian",
    "nativeName": "sardu"
  },
  "sd": {
    "name": "Sindhi",
    "nativeName": "सिन्धी, سنڌي، سندھی‎"
  },
  "se": {
    "name": "Northern Sami",
    "nativeName": "Davvisámegiella"
  },
  "sm": {
    "name": "Samoan",
    "nativeName": "gagana faa Samoa"
  },
  "sg": {
    "name": "Sango",
    "nativeName": "yângâ tî sängö"
  },
  "sr": {
    "name": "Serbian",
    "nativeName": "српски језик"
  },
  "gd": {
    "name": "Scottish Gaelic; Gaelic",
    "nativeName": "Gàidhlig"
  },
  "sn": {
    "name": "Shona",
    "nativeName": "chiShona"
  },
  "si": {
    "name": "Sinhala, Sinhalese",
    "nativeName": "සිංහල"
  },
  "sk": {
    "name": "Slovak",
    "nativeName": "slovenčina"
  },
  "sl": {
    "name": "Slovene",
    "nativeName": "slovenščina"
  },
  "so": {
    "name": "Somali",
    "nativeName": "Soomaaliga, af Soomaali"
  },
  "st": {
    "name": "Southern Sotho",
    "nativeName": "Sesotho"
  },
  "es": {
    "name": "Spanish",
    "nativeName": "Español"
  },
  "su": {
    "name": "Sundanese",
    "nativeName": "Basa Sunda"
  },
  "sw": {
    "name": "Swahili",
    "nativeName": "Kiswahili"
  },
  "ss": {
    "name": "Swati",
    "nativeName": "SiSwati"
  },
  "sv": {
    "name": "Swedish",
    "nativeName": "svenska"
  },
  "ta": {
    "name": "Tamil",
    "nativeName": "தமிழ்"
  },
  "te": {
    "name": "Telugu",
    "nativeName": "తెలుగు"
  },
  "tet": {
    "name": "Tetum",
    "nativeName": "Tetum"
  },
  "tg": {
    "name": "Tajik",
    "nativeName": "тоҷикӣ, toğikī, تاجیکی‎"
  },
  "th": {
    "name": "Thai",
    "nativeName": "ไทย"
  },
  "ti": {
    "name": "Tigrinya",
    "nativeName": "ትግርኛ"
  },
  "bo": {
    "name": "Tibetan Standard, Tibetan, Central",
    "nativeName": "བོད་ཡིག"
  },
  "tpi": {
    "name": "Tok Pisin",
    "nativeName": "Tok Pisin"
  },
  "tk": {
    "name": "Turkmen",
    "nativeName": "Türkmen, Түркмен"
  },
  "tl": {
    "name": "Tagalog",
    "nativeName": "Wikang Tagalog"
  },
  "tn": {
    "name": "Tswana",
    "nativeName": "Setswana"
  },
  "to": {
    "name": "Tonga (Tonga Islands)",
    "nativeName": "faka Tonga"
  },
  "tr": {
    "name": "Turkish",
    "nativeName": "Türkçe"
  },
  "ts": {
    "name": "Tsonga",
    "nativeName": "Xitsonga"
  },
  "tt": {
    "name": "Tatar",
    "nativeName": "татарча, tatarça, تاتارچا‎"
  },
  "tw": {
    "name": "Twi",
    "nativeName": "Twi"
  },
  "ty": {
    "name": "Tahitian",
    "nativeName": "Reo Tahiti"
  },
  "ug": {
    "name": "Uighur, Uyghur",
    "nativeName": "Uyƣurqə, ئۇيغۇرچە‎"
  },
  "uk": {
    "name": "Ukrainian",
    "nativeName": "українська"
  },
  "ur": {
    "name": "Urdu",
    "nativeName": "اردو"
  },
  "uz": {
    "name": "Uzbek",
    "nativeName": "zbek, Ўзбек, أۇزبېك‎"
  },
  "ve": {
    "name": "Venda",
    "nativeName": "Tshivenḓa"
  },
  "vi": {
    "name": "Vietnamese",
    "nativeName": "Tiếng Việt"
  },
  "vo": {
    "name": "Volapük",
    "nativeName": "Volapük"
  },
  "wa": {
    "name": "Walloon",
    "nativeName": "Walon"
  },
  "cy": {
    "name": "Welsh",
    "nativeName": "Cymraeg"
  },
  "wo": {
    "name": "Wolof",
    "nativeName": "Wollof"
  },
  "fy": {
    "name": "Western Frisian",
    "nativeName": "Frysk"
  },
  "xh": {
    "name": "Xhosa",
    "nativeName": "isiXhosa"
  },
  "yi": {
    "name": "Yiddish",
    "nativeName": "ייִדיש"
  },
  "yo": {
    "name": "Yoruba",
    "nativeName": "Yorùbá"
  },
  "za": {
    "name": "Zhuang, Chuang",
    "nativeName": "Saɯ cueŋƅ, Saw cuengh"
  }
};

});

loader.register('akvo-flow/controllers/maps-controllers-common', function(require) {
/**
  Controllers related to the map tab
  Definition:
    "placemark" is an FLOW object that represents a single survey point.
    "marker" is a map object that is rendered as a pin. Each marker have
      a placemark counterpart.
**/

FLOW.MapsController = Ember.ArrayController.extend({
    content: null,
    map: null,
    marker: null,
    markerCoordinates: null,
    questions: null,
    geocellCache: [],
    currentGcLevel: null,
    allPlacemarks: null,
    selectedMarker:null,
    selectedSI: null,
    questionAnswers: null,
    namedMap: null,
    surveyDataLayer: null,

    populateMap: function () {
        var gcLevel, placemarks, placemarkArray=[];
        if (this.content.get('isLoaded') === true) {
            gcLevel = this.get('currentGcLevel');
            // filter placemarks
            placemarks = FLOW.store.filter(FLOW.Placemark, function(item){
                return item.get('level') == gcLevel;
            });

            placemarks.forEach(function (placemark) {
                marker = this.addMarker(placemark);
                placemarkArray.push(marker);
            }, this);

            if (!Ember.none(this.allPlacemarks)){
                this.allPlacemarks.clearLayers();
            }

            this.allPlacemarks = L.layerGroup(placemarkArray);
            this.allPlacemarks.addTo(this.map);
        }
    }.observes('this.content.isLoaded'),

    adaptMap: function(bestBB, zoomlevel){
        var bbString = "", gcLevel, listToRetrieve = [];

        // determine the geocell cluster level we want to show
        if (zoomlevel < 4) {
            gcLevel = 2;
        } else if (zoomlevel < 6) {
            gcLevel = 3;
        } else if (zoomlevel < 8) {
            gcLevel = 4;
        } else if (zoomlevel < 11) {
            gcLevel = 5;
        } else {
            gcLevel = 0;
        }
        this.set('currentGcLevel',gcLevel);
        // on zoomlevel 2, the map repeats itself, leading to wrong results
        // therefore, we force to download the highest level on all the world.
        if (zoomlevel == 2) {
            bestBB = "0123456789abcdef".split("");
        }

        // see if we already have it in the cache
        // in the cache, we use a combination of geocell and gcLevel requested as the key:
        // for example "af-3", "4ee-5", etc.
        // TODO this is not optimal at high zoom levels, as we will already have loaded the same points on a level before
        for (var i = 0; i < bestBB.length; i++) {
            if (this.get('geocellCache').indexOf(bestBB[i]+"-"+gcLevel) < 0 ) {
                // if we don't have it in the cache add it to the list of items to be loaded
                listToRetrieve.push(bestBB[i]);

                // now add this key to cache
                this.get('geocellCache').push(bestBB[i] + "-" + gcLevel);
            }
        }

        // pack best bounding box values in a string for sending to the server
        bbString = listToRetrieve.join(',');

        // go get it in the datastore
        // when the points come in, populateMap will trigger and place the points
        if (!Ember.empty(bbString)) {
            this.set('content',FLOW.store.findQuery(FLOW.Placemark,
                {bbString: bbString, gcLevel: gcLevel}));
        } else {
            // we might have stuff in cache, so draw anyway
            this.populateMap();
        }
    },

    addMarker: function (placemark) {
        var marker;
        if (placemark.get('level') > 0){
            count = placemark.get('count');
            if (count == 1) {
                marker = L.circleMarker([placemark.get('latitude'),placemark.get('longitude')],{
                    radius:7,
                    color:'#d46f12',
                    fillColor:'#edb660',
                    opacity:0.9,
                    fillOpacity:0.7,
                    placemarkId: placemark.get('detailsId'),
                    collectionDate:placemark.get('collectionDate')});
                marker.on('click', onMarkerClick);
                return marker;
            }

            myIcon = L.divIcon({
                html: '<div><span>' + count + '</span></div>',
                className: 'marker-cluster',
                iconSize: new L.Point(40, 40)});

            marker = L.marker([placemark.get('latitude'),placemark.get('longitude')], {
                icon: myIcon,
                placemarkId: placemark.get('keyId')});
            return marker;
        } else {
            // if we are here, we are at level 0, and we have normal placemark icons.
            marker = L.circleMarker([placemark.get('latitude'),placemark.get('longitude')],{
                radius:7,
                color:'#d46f12',
                fillColor:'#edb660',
                placemarkId: placemark.get('detailsId'),
                collectionDate:placemark.get('collectionDate')});
            marker.on('click', onMarkerClick);
            return marker;
        }

        function onMarkerClick(marker){
            // first deselect others
            if (!Ember.none(FLOW.router.mapsController.get('selectedMarker'))) {
                if (FLOW.router.mapsController.selectedMarker.target.options.placemarkId != marker.target.options.placemarkId) {
                    FLOW.router.mapsController.selectedMarker.target.options.selected = false;
                    FLOW.router.mapsController.selectedMarker.target.setStyle({
                        color:'#d46f12',
                        fillColor:'#edb660'});
                    FLOW.router.mapsController.set('selectedMarker',null);
                }
            }

            // now toggle this one
            if (marker.target.options.selected) {
                marker.target.setStyle({
                    color:'#d46f12',
                    fillColor:'#edb660'});
                marker.target.options.selected = false;
                FLOW.router.mapsController.set('selectedMarker',null);
            } else {
                marker.target.setStyle({
                    color:'#d46f12',
                    fillColor:'#433ec9'});
                marker.target.options.selected = true;
                FLOW.router.mapsController.set('selectedMarker',marker);
            }
        }
    },

    loadNamedMap: function(formId){
        var self = this;
        //TODO Clear map
        this.loadQuestions(formId); //Load questions
        var namedMapObject = {};
        namedMapObject['mapName'] = 'raw_data_'+formId;
        namedMapObject['tableName'] = 'raw_data_'+formId;
        namedMapObject['interactivity'] = ['lat','lon','id'];
        namedMapObject['query'] = 'SELECT * FROM raw_data_'+formId;

        this.namedMapCheck(namedMapObject, formId);
    },

    /*Check if a named map exists. If one exists, call function to overlay it
    else call function to create a new one*/
    namedMapCheck: function(namedMapObject, formId){
        var self = this;
        $.get("/rest/cartodb/named_maps", function(data, status) {
            if (data.template_ids) {
                var mapExists = false;
                for (var i=0; i<data['template_ids'].length; i++) {
                    if (data['template_ids'][i] === namedMapObject.mapName) {
                        //named map already exists
                        mapExists = true;
                        self.set('namedMap', namedMapObject.mapName);
                        break;
                    }
                }

                if (!mapExists) {
                    //create new named map
                    self.createNamedMap(namedMapObject, formId);
                }
            }
        });
    },

    //create named map
    createNamedMap: function(namedMapObject, formId){
        var self = this;

        //style of points for new layer
        var cartocss = "#"+namedMapObject.tableName+"{"
        +"marker-fill-opacity: 0.9;"
        +"marker-line-color: #FFF;"
        +"marker-line-width: 1.5;"
        +"marker-line-opacity: 1;"
        +"marker-placement: point;"
        +"marker-type: ellipse;"
        +"marker-width: 10;"
        +"marker-fill: #FF6600;"
        +"marker-allow-overlap: true;"
        +"}";

        var configJsonData = {};
        configJsonData['interactivity'] = namedMapObject.interactivity;
        configJsonData['name'] = namedMapObject.mapName;
        configJsonData['cartocss'] = cartocss;
        configJsonData['sql'] = namedMapObject.query;

        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: '/rest/cartodb/named_maps',
            data: JSON.stringify(configJsonData), //stringify the payload before sending it
            dataType: 'json',
            success: function(namedMapData){
                if (namedMapData.template_id) {
                    self.set('namedMap', namedMapData.template_id);
                }
            }
        });
    },

    /*this function overlays a named map on the cartodb map*/
    createLayer: function(formId){
        if (this.namedMap) {
            var self = this;

            // add cartodb layer with one sublayer
            cartodb.createLayer(self.map, {
                user_name: FLOW.Env.appId,
                type: 'namedmap',
                named_map: {
                    name: this.namedMap,
                    layers: [{
                        layer_name: "t",
                        interactivity: "id"
                    }]
                }
            },{
                tiler_domain: FLOW.Env.cartodbHost,
                tiler_port: "", //set to empty string to stop cartodb js from appending default port
                tiler_protocol: "https",
                no_cdn: true
            })
            .addTo(self.map)
            .done(function(layer) {
                layer.setZIndex(1000); //required to ensure that the cartodb layer is not obscured by the here maps base layers
                self.set('surveyDataLayer', layer);

                self.addCursorInteraction(layer);

                var dataLayer = layer.getSubLayer(0);
                dataLayer.setInteraction(true);

                dataLayer.on('featureClick', function(e, latlng, pos, data) {
                    self.set('markerCoordinates', [data.lat, data.lon]);

                    //get survey instance
                    FLOW.placemarkDetailController.set( 'si', FLOW.store.find(FLOW.SurveyInstance, data.id));

                    //get questions answers for clicked survey instance
                    FLOW.placemarkDetailController.set('content', FLOW.store.findQuery(FLOW.QuestionAnswer, {
                        'surveyInstanceId' : data.id
                    }));
                });
            });
        }
    }.observes('this.namedMap'),

    loadQuestions: function(formId){
        this.set('questions', FLOW.store.findQuery(FLOW.Question, {
            'surveyId' : formId
        }));
    },

    /*function is required to manage how the cursor appears on the cartodb map canvas*/
    addCursorInteraction: function (layer) {
        var hovers = [];

        layer.bind('featureOver', function(e, latlon, pxPos, data, layer) {
            hovers[layer] = 1;
            if(_.any(hovers)) {
                $('#flowMap').css('cursor', 'pointer');
            }
        });

        layer.bind('featureOut', function(m, layer) {
            hovers[layer] = 0;
            if(!_.any(hovers)) {
                $('#flowMap').css({"cursor":"-moz-grab","cursor":"-webkit-grab"});
            }
        });
    },

    clearSurveyDataLayer: function(){
        if (this.surveyDataLayer) {
            this.map.removeLayer(this.surveyDataLayer);
            this.set('surveyDataLayer', null);
        }
    },

    formatDate: function(date) {
      if (date && !isNaN(date.getTime())) {
        return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
      }
      return null;
    }
});

FLOW.placemarkDetailController = Ember.ArrayController.create({
  content: Ember.A(),
  sortProperties: ['order'],
  sortAscending: true,
  collectionDate: null,
  surveyedLocaleDisplayName: null,
  surveyedLocaleIdentifier: null,
  si: null,

  populate: function (placemarkId) {
	  if (placemarkId) {
		  this.set('content', FLOW.store.findQuery(FLOW.PlacemarkDetail, {
			  placemarkId: placemarkId
		  }));
	  } else {
		  this.set('content', Ember.A());
	  }
  },

  siDetails: function() {
      if (FLOW.Env.mapsProvider === 'cartodb') {
          this.set('surveyedLocaleDisplayName', this.si.get('surveyedLocaleDisplayName'));
          this.set('surveyedLocaleIdentifier', this.si.get('surveyedLocaleIdentifier'));
      }
      this.set('collectionDate', this.si.get('collectionDate'));
  }.observes('si.isLoaded'),

  handlePlacemarkSelection: function () {
    var selectedPlacemarkId = null;
    if (!Ember.none(FLOW.router.get('mapsController'))) {
      var mapsController = FLOW.router.get('mapsController');
      if (!Ember.none(mapsController.get('selectedMarker'))) {
      	selectedPlacemarkId = mapsController.selectedMarker.target.options.placemarkId;
      	this.set('collectionDate',mapsController.selectedMarker.target.options.collectionDate);
      }
      this.populate(selectedPlacemarkId);
    }
  }.observes('FLOW.router.mapsController.selectedMarker')

});

});

loader.register('akvo-flow/controllers/messages-controllers', function(require) {
FLOW.messageControl = Ember.ArrayController.create({
  sortProperties: null,
  sortAscending: true,
  content: null,
  sinceArray: [],

  populate: function () {
    this.get('sinceArray').clear();
    FLOW.metaControl.set('since', null);
    // put null in as the first item
    this.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.set('content', FLOW.store.findQuery(FLOW.Message, {
      'since': null
    }));
    this.set('sortProperties', ['lastUpdateDateTime']);
    this.set('sortAscending', false);
  },

  doInstanceQuery: function (since) {
    this.set('content', FLOW.store.findQuery(FLOW.Message, {
      'since': since
    }));
  },

  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
    this.set('selected', FLOW.tableColumnControl.get('selected'));
  }
});

});

loader.register('akvo-flow/controllers/permissions', function(require) {
FLOW.permControl = Ember.Controller.create({
  perms: [],

  init: function () {
    this._super();
    this.initPermissions();
    this.setUserPermissions();
    this.setCurrentPermissions();
  },

  initPermissions: function () {
    this.perms.push(Ember.Object.create({
      perm: 'createSurvey',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'editSurvey',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'uploadSurveyZipData',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'importDataReport',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'viewMessages',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'publishSurvey',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'mapData',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'setDataPrivacy',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'editRawData',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'deleteRawData',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'runReport',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'deleteSurvey',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'deleteSurveyGroup',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'addUser',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'editUser',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'deleteUser',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'approveSurvey',
      value: false
    }));
    this.perms.push(Ember.Object.create({
      perm: 'editEditorialContent',
      value: false
    }));
  },

  setUserPermissions: function () {
    var user = true;
    if (user === true) {
      this.perms.findProperty('perm', 'createSurvey').value = true;
      this.perms.findProperty('perm', 'editSurvey').value = true;
      this.perms.findProperty('perm', 'uploadSurveyZipData').value = true;
      this.perms.findProperty('perm', 'viewMessages').value = true;
      this.perms.findProperty('perm', 'publishSurvey').value = true;
      this.perms.findProperty('perm', 'mapData').value = true;
      this.perms.findProperty('perm', 'setDataPrivacy').value = true;
      this.perms.findProperty('perm', 'runReport').value = true;
    }

    if (user === true) {
      this.perms.findProperty('perm', 'createSurvey').value = true;
      this.perms.findProperty('perm', 'editSurvey').value = true;
      this.perms.findProperty('perm', 'uploadSurveyZipData').value = true;
      this.perms.findProperty('perm', 'importDataReport').value = true;
      this.perms.findProperty('perm', 'viewMessages').value = true;
      this.perms.findProperty('perm', 'publishSurvey').value = true;
      this.perms.findProperty('perm', 'mapData').value = true;
      this.perms.findProperty('perm', 'setDataPrivacy').value = true;
      this.perms.findProperty('perm', 'runReport').value = true;
      this.perms.findProperty('perm', 'editRawData').value = true;
      this.perms.findProperty('perm', 'deleteRawData').value = true;
      this.perms.findProperty('perm', 'approveSurvey').value = true;
    }

    if (user === true) {
      this.perms.findProperty('perm', 'createSurvey').value = true;
      this.perms.findProperty('perm', 'editSurvey').value = true;
      this.perms.findProperty('perm', 'uploadSurveyZipData').value = true;
      this.perms.findProperty('perm', 'importDataReport').value = true;
      this.perms.findProperty('perm', 'viewMessages').value = true;
      this.perms.findProperty('perm', 'publishSurvey').value = true;
      this.perms.findProperty('perm', 'mapData').value = true;
      this.perms.findProperty('perm', 'setDataPrivacy').value = true;
      this.perms.findProperty('perm', 'runReport').value = true;
      this.perms.findProperty('perm', 'editRawData').value = true;
      this.perms.findProperty('perm', 'deleteRawData').value = true;
      this.perms.findProperty('perm', 'approveSurvey').value = true;
      this.perms.findProperty('perm', 'deleteSurvey').value = true;
      this.perms.findProperty('perm', 'deleteSurveyGroup').value = true;
      this.perms.findProperty('perm', 'addUser').value = true;
      this.perms.findProperty('perm', 'editUser').value = true;
      this.perms.findProperty('perm', 'deleteUser').value = true;
    }

  },

  setCurrentPermissions: function () {
    this.perms.forEach(function (item) {
      //this.set(item.perm,item.value);
    });
  },

  /* Given an entity, process the permissions settings for the current user
    and return the permissions associated with that entity.  Entity is an Ember object*/
  permissions: function(entity) {
    var keyId, ancestorIds, permissions = [], currentUserPermissions = FLOW.currentUser.get('pathPermissions');

    if (!currentUserPermissions || !entity) { return []; }

    // return superAdmin permissions
    if ("0" in currentUserPermissions){
      return currentUserPermissions["0"];
    }

    // first check current object id
    keyId = entity.get('keyId');
    if (keyId in currentUserPermissions) {
      permissions = currentUserPermissions[keyId];
    }

    // check ancestor permissions
    ancestorIds = entity.get('ancestorIds');
    if (!ancestorIds) {
      return permissions;
    }

    var i;
    for(i = 0; i < ancestorIds.length; i++){
      if (ancestorIds[i] in currentUserPermissions) {
        if (currentUserPermissions[ancestorIds[i]]) {
          currentUserPermissions[ancestorIds[i]].forEach(function(item){
            if (permissions.indexOf(item) < 0) {
              permissions.push(item);
            }
          });
        }
      }
    }

    return permissions;
  },

  /* query based on survey (group) ancestorIds whether a user has
  permissions for data deletion */
  canDeleteData: function(ancestorIds) {
      var pathPermissions = FLOW.currentUser.get('pathPermissions');
      var canDelete = false;
      ancestorIds.forEach(function(id){
          if(id in pathPermissions && pathPermissions[id].indexOf("DATA_DELETE") > -1) {
              canDelete = true;
          }
      });
      return canDelete;
    },

  /* takes a survey (ember object) and checks whether the current user
    has edit permissions for the survey */
  canEditSurvey: function(survey) {
    var permissions;
    if (!Ember.none(survey)) {
      permissions = this.permissions(survey);
    }
    return permissions && permissions.indexOf("PROJECT_FOLDER_UPDATE") > -1;
  },

  /* takes a form (ember object) and checks with user permissions
  whether the current user has edit permissions for the form */
  canEditForm: function(form) {
    var permissions;
    if (!Ember.none(form)) {
      permissions = this.permissions(form);
    }
    return permissions && permissions.indexOf("FORM_UPDATE") > -1;
  },

  canEditResponses: function (form) {
    var permissions;
    if (!Ember.none(form)) {
      permissions = this.permissions(form);
    }
    return permissions && permissions.indexOf("DATA_UPDATE") > -1;
  },

  canManageDevices: function () {
    var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
    for (var perms in currentUserPermissions) {
      if (currentUserPermissions[perms].indexOf("DEVICE_MANAGE") > -1) {
        return true;
      }
    }
    return false;
  }.property(),

  canManageCascadeResources: function () {
    var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
    for (var perms in currentUserPermissions) {
      if (currentUserPermissions[perms].indexOf("CASCADE_MANAGE") > -1) {
          return true;
      }
    }
    return false;
  }.property(),

  canCleanData: function () {
      var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
      for (var perms in currentUserPermissions) {
          if (currentUserPermissions[perms].indexOf("DATA_CLEANING") > -1) {
              return true;
          }
      }
      return false;
  }.property(),

  canManageDataAppoval: function () {
      var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
      for (var perms in currentUserPermissions) {
          if (currentUserPermissions[perms].indexOf("DATA_APPROVE_MANAGE") > -1) {
              return true;
          }
      }
      return false;
  }.property(),
});


FLOW.dialogControl = Ember.Object.create({
  delS: "delS",
  delQG: "delQG",
  delQ: "delQ",
  delUser: "delUser",
  delAssignment: "delAssignment",
  delDeviceGroup: "delDeviceGroup",
  delSI: "delSI",
  delSI2: "delSI2",
  delCR: "delCR",
  delForm: "delForm",
  showDialog: false,
  message: null,
  header: null,
  activeView: null,
  activeAction: null,
  showOK: true,
  showCANCEL: true,

  confirm: function (event) {
    this.set('activeView', event.view);
    this.set('activeAction', event.context);
    this.set('showOK', true);
    this.set('showCANCEL', true);

    switch (this.get('activeAction')) {
    case "delS":
      this.set('header', Ember.String.loc('_s_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delQG":
      this.set('header', Ember.String.loc('_qg_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delQ":
      this.set('header', Ember.String.loc('_q_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delUser":
      this.set('header', Ember.String.loc('_are_you_sure_you_want_to_delete_this_user'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delAssignment":
      this.set('header', Ember.String.loc('_assignment_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delDeviceGroup":
      this.set('header', Ember.String.loc('_device_group_delete_header'));
      this.set('message', Ember.String.loc('_this_cant_be_undo'));
      this.set('showDialog', true);
      break;

    case "delSI":
      this.set('header', Ember.String.loc('_delete_record_header'));
      this.set('message', Ember.String.loc('_are_you_sure_delete_this_data_record'));
      this.set('showDialog', true);
      break;

    case "delSI2":
      this.set('header', Ember.String.loc('_delete_record_header'));
      this.set('showDialog', true);
      break;

    case "delForm":
      this.set('header', "Delete form");
      this.set('message', "Are you sure you want to delete this form?");
      this.set('showDialog', true);
      break;

    case "delCR":
        this.set('header', Ember.String.loc('_delete_cascade_resource_header'));
        this.set('message', Ember.String.loc('_delete_cascade_resource_text'));
        this.set('showDialog', true);
      break;

    default:
    }
  },

  doOK: function (event) {
    this.set('header', null);
    this.set('message', null);
    this.set('showCANCEL', true);
    this.set('showDialog', false);
    var view = this.get('activeView');
    switch (this.get('activeAction')) {
    case "delS":
      view.deleteSurvey.apply(view, arguments);
      break;

    case "delQG":
      view.deleteQuestionGroup.apply(view, arguments);
      break;

    case "delQ":
      this.set('showDialog', false);
      view.deleteQuestion.apply(view, arguments);
      break;

    case "delUser":
      this.set('showDialog', false);
      view.deleteUser.apply(view, arguments);
      break;

    case "delAssignment":
      this.set('showDialog', false);
      view.deleteSurveyAssignment.apply(view, arguments);
      break;

    case "delDeviceGroup":
      this.set('showDialog', false);
      view.deleteDeviceGroup.apply(view, arguments);
      break;

    case "delSI":
      this.set('showDialog', false);
      view.deleteSI.apply(view, arguments);
      break;

    case "delSI2":
      this.set('showDialog', false);
      view.deleteSI.apply(view, arguments);
      break;
      
    case "delForm":
      this.set('showDialog', false);
      FLOW.surveyControl.deleteForm();
      break;
      
    case "delCR":
        this.set('showDialog', false);
        view.deleteResource(view, arguments);
        break;

    default:
    }
  },

  doCANCEL: function (event) {
    this.set('showDialog', false);
  }
});

});

loader.register('akvo-flow/controllers/reports-controllers', function(require) {
FLOW.surveyQuestionSummaryControl = Ember.ArrayController.create({
  content: null,

  doSurveyQuestionSummaryQuery: function (questionId) {
    this.set('content', FLOW.store.find(FLOW.SurveyQuestionSummary, {
      'questionId': questionId
    }));
  }
});

FLOW.chartDataControl = Ember.Object.create({
  questionText: "",
  maxPer: null,
  chartData: [],
  smallerItems: [],
  total: null
});

FLOW.chartTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_doughnut_chart'),
      value: "doughnut"
    }), Ember.Object.create({
      label: Ember.String.loc('_vertical_bar_chart'),
      value: "vbar"
    }),
    Ember.Object.create({
      label: Ember.String.loc('_horizontal_bar_chart'),
      value: "hbar"
    })
  ]
});

});

loader.register('akvo-flow/controllers/survey-controllers', function(require) {

FLOW.questionTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_free_text'),
      value: 'FREE_TEXT'
    }), Ember.Object.create({
      label: Ember.String.loc('_option'),
      value: 'OPTION'
    }), Ember.Object.create({
        label: Ember.String.loc('_cascade'),
     value: 'CASCADE'
      }),Ember.Object.create({
      label: Ember.String.loc('_number'),
      value: 'NUMBER'
    }), Ember.Object.create({
      label: Ember.String.loc('_gelocation'),
      value: 'GEO'
    }), Ember.Object.create({
      label: Ember.String.loc('_photo'),
      value: 'PHOTO'
    }), Ember.Object.create({
      label: Ember.String.loc('_video'),
      value: 'VIDEO'
    }), Ember.Object.create({
      label: Ember.String.loc('_date'),
      value: 'DATE'
    }), Ember.Object.create({
      label: Ember.String.loc('_barcode'),
      value: 'SCAN'
    }), Ember.Object.create({
      label: Ember.String.loc('_geoshape'),
      value: 'GEOSHAPE'
    }), Ember.Object.create({
      label: Ember.String.loc('_signature'),
      value: 'SIGNATURE'
    }), Ember.Object.create({
        label: Ember.String.loc('_caddisfly'),
        value: 'CADDISFLY'
      })
  ]
});


FLOW.notificationOptionControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_link'),
      value: "LINK"
    }), Ember.Object.create({
      label: "attachment",
      value: "ATTACHMENT"
    })
  ]
});

FLOW.notificationTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_email'),
      value: "EMAIL"
    })
  ]
});

FLOW.notificationEventControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_raw_data_reports_nightly'),
      value: "rawDataReport"
    }), Ember.Object.create({
      label: Ember.String.loc('_survey_submission'),
      value: "surveySubmission"
    }), Ember.Object.create({
      label: Ember.String.loc('_survey_approval'),
      value: "surveyApproval"
    })
  ]
});

FLOW.languageControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: "English",
      value: "en"
    }), Ember.Object.create({
      label: "Español",
      value: "es"
    }), Ember.Object.create({
      label: "Français",
      value: "fr"
    })
  ]
});

FLOW.surveyPointTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: Ember.String.loc('_point'),
      value: "Point"
    }), Ember.Object.create({
      label: Ember.String.loc('_household'),
      value: "Household"
    }), Ember.Object.create({
      label: Ember.String.loc('_public_institution'),
      value: "PublicInstitution"
    })
  ]
});

FLOW.surveySectorTypeControl = Ember.Object.create({
  content: [
    Ember.Object.create({
      label: "Water and Sanitation",
      value: "WASH"
    }), Ember.Object.create({
      label: "Education",
      value: "EDUC"
    }), Ember.Object.create({
      label: "Economic development",
      value: "ECONDEV"
    }), Ember.Object.create({
      label: "Health care",
      value: "HEALTH"
    }), Ember.Object.create({
      label: "IT and Communication",
      value: "ICT"
    }), Ember.Object.create({
      label: "Food security",
      value: "FOODSEC"
    }), Ember.Object.create({
      label: "Other",
      value: "OTHER"
    })
  ]
});

FLOW.privacyLevelControl = Ember.Object.create({
  content: ["PRIVATE", "PUBLIC"]
});

FLOW.alwaysTrue = function () {
  return true;
};

FLOW.surveyGroupControl = Ember.ArrayController.create({
  sortProperties: ['code'],
  sortAscending: true,
  content: null,

  setFilteredContent: function (f) {
    this.set('content', FLOW.store.filter(FLOW.SurveyGroup, f));
  },

  // load all Survey Groups
  populate: function (f) {
    var fn = (f && $.isFunction(f) && f) || FLOW.alwaysTrue;
    FLOW.store.find(FLOW.SurveyGroup);
    this.setFilteredContent(fn);
  },

  // checks if data store contains surveys within this survey group.
  // this is also checked server side.
  containsSurveys: function () {
    var surveys, sgId;
    surveys = FLOW.store.filter(FLOW.Survey, function (data) {
      sgId = FLOW.selectedControl.selectedSurveyGroup.get('id');
      if (data.get('surveyGroupId') == sgId) {
        return true;
      }
    });
    return surveys.get('content').length > 0;
  },

  deleteSurveyGroup: function (keyId) {
    var surveyGroup;
    surveyGroup = FLOW.store.find(FLOW.SurveyGroup, keyId);
    surveyGroup.deleteRecord();
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedSurveyGroup', null);
  },

  /* return all the ancestor paths of a given path string */
  ancestorPaths: function(pathString) {
    if(!pathString) {
        return [];
    }

    var ancestors = [];
    while(pathString) {
        ancestors.push(pathString);
        pathString = pathString.slice(0, pathString.lastIndexOf("/"));
    }
    ancestors.push("/"); // add the root level folder to ancestors list
    return ancestors;
  },

  /* retrieve a survey group based on its id and check based on its
  path whether or not a user is able to delete data in the group. Used
  for monitoring groups */
  userCanDeleteData: function(surveyGroupId) {
    var ancestorIds;
    var surveyGroups = FLOW.store.filter(FLOW.SurveyGroup, function(sg){
        return sg.get('keyId') === surveyGroupId;
    });

    if(surveyGroups && surveyGroups.get('firstObject')) {
        ancestorIds = surveyGroups.get('firstObject').get('ancestorIds');
        return FLOW.permControl.canDeleteData(ancestorIds);
    } else {
        return false; // need survey group and ancestorIds, otherwise prevent delete
    }
  },
});


/**
 * The root project folder is represented as null with the keyId null
 */
FLOW.projectControl = Ember.ArrayController.create({
  content: null,
  currentProject: null,
  moveTarget: null,
  isLoading: true,

  populate: function() {
    FLOW.store.find(FLOW.SurveyGroup);
    this.set('content', FLOW.store.filter(FLOW.SurveyGroup, function(p) {
      return true;
    }));
  },

  setCurrentProject: function(project) {
    this.set('currentProject', project);
    window.scrollTo(0,0);
  },

  /* return true if the given SurveyGroup's has the data cleaning permission
   * associated with it, or if one of the ancestors or descendants of the SurveyGroup
   * has data cleaning permission associated with it.  In the case of descendants we
   * return true in order to be able to browse to the descendant */
  dataCleaningEnabled: function(surveyGroup) {
    var permissions = FLOW.currentUser.get('pathPermissions');
    var keyedSurvey;

    for (var key in permissions) {
      if(permissions[key].indexOf("DATA_CLEANING") > -1){
        // check key against survey group
        if(surveyGroup.get('keyId') === +key) {
          return true;
        }

        // check ancestors to for matching permission from higher level in hierarchy
        var ancestorIds = surveyGroup.get('ancestorIds');
        if (ancestorIds === null) {
          return false;
        } else {
          for(var i = 0; i < ancestorIds.length; i++){
            if(ancestorIds[i] === +key) {
              return true;
            }
          }
        }

        // finally check for all descendents that may have surveyGroup.keyId in their
        // ancestor list otherwise will not be able to browse to them.
        keyedSurvey = FLOW.store.find(FLOW.SurveyGroup, key);
        if (keyedSurvey) {
          var keyedAncestorIds = keyedSurvey.get('ancestorIds');
          if (keyedAncestorIds === null) {
            return false;
          } else {
            for (var j = 0; j < keyedAncestorIds.length; j++) {
              if(keyedAncestorIds[j] === surveyGroup.get('keyId')) {
                return true;
              }
            }
          }
        }
      }
    }
    return false;
  },

  /* Computed properties */
  breadCrumbs: function() {
    var result = [];
    var currentProject = this.get('currentProject');
    if (currentProject === null) {
      // current project is root
      return [];
    }
    var id = currentProject.get('keyId');
    while(id !== null && id !== 0) {
      project = FLOW.store.find(FLOW.SurveyGroup, id);
      result.push(project);
      id = project.get('parentId');
    }
    return result.reverse();
  }.property('@each', 'currentProject'),

  currentFolders: function() {
    var self = this;
    var currentProject = this.get('currentProject');
    var parentId = currentProject ? currentProject.get('keyId') : 0;
    return this.get('content').filter(function(project) {
      return project.get('parentId') === parentId;
    }).sort(function(a, b) {
      if (self.isProjectFolder(a) && self.isProject(b)) {
        return -1;
      } else if (self.isProject(a) && self.isProjectFolder(b)) {
        return 1;
      } else {
        var aCode = a.get('code') || a.get('name');
        var bCode = b.get('code') || b.get('name');
        if (aCode === bCode) return 0;
        if (aCode === 'New survey' || aCode === 'New folder') return -1;
        if (bCode === 'New survey' || bCode === 'New folder') return 1;
        return aCode.localeCompare(bCode);
      }
    });
  }.property('@each', 'currentProject', 'moveTarget'),

  formCount: function() {
    return FLOW.surveyControl.content && FLOW.surveyControl.content.get('length') || 0;
  }.property('FLOW.surveyControl.content.@each'),

  questionCount: function () {
    var questions = FLOW.questionControl.filterContent;
    return questions && questions.get('length') || 0;
  }.property('FLOW.questionControl.filterContent.@each'),

  hasForms: function() {
    return this.get('formCount') > 0;
  }.property('this.formCount'),

  currentProjectPath: function() {
    var projectList = this.get('breadCrumbs');
    if(projectList.length === 0) {
        return ""; // root project folder
    } else {
        var path = "";
        for(i = 0; i < projectList.length; i++){
            path += "/" + projectList[i].get('name');
        }
        return path;
    }
  }.property('breadCrumbs'),

  currentFolderPermissions: function() {
      var currentFolder = this.get('currentProject');
      var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
      var folderPermissions = [];

      if (!currentUserPermissions) {
        return [];
      }

      // root folder
      if (!currentFolder) {
        if (currentUserPermissions[0]) {
          currentUserPermissions[0].forEach(function(item){
            folderPermissions.push(item);
          });
        }
        return folderPermissions;
      }

      // first check current object id
      if (currentFolder.get('keyId') in currentUserPermissions) {
        currentUserPermissions[currentFolder.get('keyId')].forEach(function(item){
          folderPermissions.push(item);
        });
      }

      var ancestorIds = currentFolder.get('ancestorIds');
      if (!ancestorIds) {
        return folderPermissions;
      }

      var i;
      for(i = 0; i < ancestorIds.length; i++){
        if (ancestorIds[i] in currentUserPermissions) {
          currentUserPermissions[ancestorIds[i]].forEach(function(item){
            folderPermissions.push(item);
          });
        }
      }

      return folderPermissions;
  }.property('currentProject'),

  /* Actions */
  selectProject: function(evt) {
    var project = evt.context;
    // the target should not be openable while being moved. Prevents moving it into itself.
    if (this.moveTarget == project) {
    	return;
    }

    this.setCurrentProject(project);

    // User is using the breadcrumb to navigate, we could have unsaved changes
    FLOW.store.commit();

    if (this.isProject(project)) {
        // load caddisfly resources if they are not loaded
        // and only when surveys are selected
        this.loadCaddisflyResources();

        // applies to project where data approval has
        // been previously set
        if (project.get('requireDataApproval')) {
            this.loadDataApprovalGroups();
        }

        FLOW.selectedControl.set('selectedSurveyGroup', project);
    }

    this.set('newlyCreated', null);
  },

  selectRootProject: function() {
    this.setCurrentProject(null);
  },

  /*
   * Load caddisfly resources if they are not already loaded
   */
  loadCaddisflyResources: function () {
      var caddisflyResourceController = FLOW.router.get('caddisflyResourceController');
      if (Ember.empty(caddisflyResourceController.get('content'))) {
          caddisflyResourceController.load();
      }
  },

  /*
   * Load the data approval resources for this survey
   */
  loadDataApprovalGroups: function (survey) {
      var approvalGroups = FLOW.router.approvalGroupListController.get('content');
      if (Ember.empty(approvalGroups)) {
          FLOW.router.approvalGroupListController.load();
      }
  },

  /* Create a new project folder. The current project must be root or a project folder */
  createProjectFolder: function() {
    this.createNewProject(true);
  },

  createProject: function() {
    this.createNewProject(false);
  },

  createNewProject: function(folder) {
    var currentFolder = this.get('currentProject');
    var currentFolderId = currentFolder ? currentFolder.get('keyId') : 0;

    var name = folder ? Ember.String.loc('_new_folder').trim() : Ember.String.loc('_new_survey').trim();
    var projectType = folder ? "PROJECT_FOLDER" : "PROJECT";
    var path = this.get('currentProjectPath') + "/" + name;

    var newRecord = FLOW.store.createRecord(FLOW.SurveyGroup, {
      "code": name,
      "name": name,
      "path": path,
      "parentId": currentFolderId,
      "projectType": projectType
    });
    FLOW.store.commit();

    this.set('newlyCreated', newRecord);
  },

  deleteProject: function(evt) {
    var project = FLOW.store.find(FLOW.SurveyGroup, evt.context.get('keyId'));
    project.deleteRecord();
    FLOW.store.commit();
  },
  
  /* start moving a folder. Confusingly, the target is what will move */
  beginMoveProject: function(evt) {
    this.set('newlyCreated', null);
    this.set('moveTarget', evt.context);
    this.set('moveTargetType', this.isProjectFolder(evt.context) ? "folder" : "survey");
  },

  beginCopyProject: function(evt) {
    this.set('newlyCreated', null);
    this.set('copyTarget', evt.context);
  },

  cancelMoveProject: function(evt) {
    this.set('moveTarget', null);
  },

  cancelCopyProject: function(evt) {
    this.set('copyTarget', null);
  },

  endMoveProject: function(evt) {
    var newFolderId = this.get('currentProject') ? this.get('currentProject').get('keyId') : 0;
    var project = this.get('moveTarget');
    var path = this.get('currentProjectPath') + "/" + project.get('name');
    project.set('parentId', newFolderId);
    project.set('path', path);
    FLOW.store.commit();
    this.set('moveTarget', null);
  },

  endCopyProject: function(evt) {
    var currentFolder = this.get('currentProject');

    FLOW.store.findQuery(FLOW.Action, {
      action: 'copyProject',
      targetId: this.get('copyTarget').get('keyId'),
      folderId: currentFolder ? currentFolder.get('keyId') : 0,
    });

    FLOW.store.commit();

    FLOW.dialogControl.set('activeAction', "ignore");
    FLOW.dialogControl.set('header', Ember.String.loc('_copying_survey'));
    FLOW.dialogControl.set('message', Ember.String.loc('_copying_published_text_'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);

    this.set('copyTarget', null);
  },

  /* Helper methods */
  isProjectFolder: function(project) {
    return project === null || project.get('projectType') === 'PROJECT_FOLDER';
  },

  isProject: function(project) {
    return !this.isProjectFolder(project);
  },

  isProjectFolderEmpty: function(folder) {
    var id = folder.get('keyId');
    var children = this.get('content').filter(function(project) {
      return project.get('parentId') === id;
    });
    return children.get('length') === 0;
  },

  /*
   * A computed property to enable editing and displaying
   * the selected approval group for a survey, as well as
   * loading the appropriate approval steps depending on
   * the selected approval group
   */
  dataApprovalGroup: function (key, value, previousValue) {
      var survey = this.get('currentProject');

      // setter
      if (arguments.length > 1 && survey) {
          survey.set('dataApprovalGroupId', value && value.get('keyId'));
      }

      // getter
      var approvalGroupId = survey && survey.get('dataApprovalGroupId');
      FLOW.router.approvalStepsController.loadByGroupId(approvalGroupId);

      var groups = FLOW.router.approvalGroupListController.get('content');
      return groups && groups.filterProperty('keyId', approvalGroupId).get('firstObject');
  }.property('this.currentProject.dataApprovalGroupId'),

  saveProject: function() {
    var currentProject = this.get('currentProject');
    var currentForm = FLOW.selectedControl.get('selectedSurvey');

    if (currentProject && currentProject.get('isDirty')) {
      var name = currentProject.get('name').trim();
      currentProject.set('name', name);
      currentProject.set('code', name);
      currentProject.set('path', this.get('currentProjectPath'));
    }

    if (currentForm && currentForm.get('isDirty')) {
      var name = currentForm.get('name').trim();
      currentForm.set('name', name);
      currentForm.set('code', name);
      var path = this.get('currentProjectPath') + "/" + name;
      currentForm.set('path', path);
      currentForm.set('status', 'NOT_PUBLISHED');
    }

    FLOW.store.commit();
  }
});


FLOW.surveyControl = Ember.ArrayController.create({
  content: null,
  publishedContent: null,
  sortProperties: ['name'],
  sortAscending: true,

  setPublishedContent: function () {
    var sgId;
    if (FLOW.selectedControl.get('selectedSurveyGroup') && FLOW.selectedControl.selectedSurveyGroup.get('keyId') > 0) {
      sgId = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
      this.set('publishedContent', Ember.ArrayController.create({
        sortProperties: this.get('sortProperties'),
        sortAscending: this.get('sortAscending'),
        content: FLOW.store.filter(FLOW.Survey, function (item) {
          return item.get('surveyGroupId') == sgId && item.get('status') == 'PUBLISHED';
        })
      }));
    } else {
      this.set('publishedContent', null);
    }
  }.observes('FLOW.selectedControl.selectedSurveyGroup'),

  populate: function () {

    var id;
    if (FLOW.selectedControl.get('selectedSurveyGroup')) {
      id = FLOW.selectedControl.selectedSurveyGroup.get('keyId');
      // this content is actualy not used, the data ends up in the store
      // and is accessed through the filtered content above
      this.set('content', FLOW.store.findQuery(FLOW.Survey, {
        surveyGroupId: id
      }));

    } else {
      this.set('content', null);
    }
  }.observes('FLOW.selectedControl.selectedSurveyGroup'),

  selectFirstForm: function() {
    if(FLOW.selectedControl.selectedSurvey) return; // ignore if form is already selected
    if (this.get('content') && this.content.get('isLoaded')) {
      var form = this.content.get('firstObject');
      if (form) {
        FLOW.selectedControl.set('selectedSurvey', form);
      }
    }
  }.observes('content.isLoaded'),

  refresh: function () {
	  var sg = FLOW.selectedControl.get('selectedSurveyGroup');
	  this.set('content', FLOW.store.filter(FLOW.Survey, function (item) {
		  return item.get('surveyGroupId') === sg.get('keyId');
	  }));
  },

  newLocale: function () {
	  var newLocaleId = FLOW.selectedControl && FLOW.selectedControl.selectedSurveyGroup && FLOW.selectedControl.selectedSurveyGroup.get('newLocaleSurveyId');
	  if(!this.get('content') || !this.get('content').get('isLoaded')) { return; }
	  this.set('newLocaleSurvey', this.find(function (item) { return item.get('keyId') === newLocaleId; }));
  }.observes('content.isLoaded'),

  publishSurvey: function () {
    var surveyId;
    surveyId = FLOW.selectedControl.selectedSurvey.get('keyId');
    FLOW.store.findQuery(FLOW.Action, {
      action: 'publishSurvey',
      surveyId: surveyId
    });

    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_publishing_survey'));
    FLOW.dialogControl.set('message', Ember.String.loc('_survey_published_text_'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  createForm: function() {
    var code = Ember.String.loc('_new_form').trim();
    var path = FLOW.projectControl.get('currentProjectPath') + "/" + code;
    FLOW.store.createRecord(FLOW.Survey, {
      "name": code,
      "code": code,
      "path": path,
      "defaultLanguageCode": "en",
      "requireApproval": false,
      "status": "NOT_PUBLISHED",
      "surveyGroupId": FLOW.selectedControl.selectedSurveyGroup.get('keyId'),
      "version":"1.0"
    });
    FLOW.projectControl.get('currentProject').set('deleteDisabled', true);
    FLOW.store.commit();
    this.refresh();
  },

  deleteForm: function() {
    var keyId = FLOW.selectedControl.selectedSurvey.get('keyId');
    var survey = FLOW.store.find(FLOW.Survey, keyId);
    if (FLOW.projectControl.get('formCount') === 1) {
      FLOW.projectControl.get('currentProject').set('surveyList', null);
      FLOW.projectControl.get('currentProject').set('deleteDisabled', false);
    }
    survey.deleteRecord();

    FLOW.store.commit();
    this.refresh();
  },

  showPreview: function() {
    FLOW.previewControl.set('showPreviewPopup', true);
  },

  selectForm: function(evt) {
    FLOW.selectedControl.set('selectedSurvey', evt.context);
    //  we don't allow copying or moving between forms
    FLOW.selectedControl.set('selectedForMoveQuestionGroup',null);
    FLOW.selectedControl.set('selectedForCopyQuestionGroup',null);
    FLOW.selectedControl.set('selectedForMoveQuestion',null);
    FLOW.selectedControl.set('selectedForCopyQuestion',null);
  },

  /* retrieve a survey and check based on its path whether the user
  is allowed to delete survey instances related to the survey */
  userCanDeleteData: function(surveyId) {
    var survey;
    this.get('content').forEach(function(item){
        if(item.get('keyId') === surveyId) {
            survey = item;
        }
    });

    if(survey && survey.get('path')) {
        return FLOW.permControl.canDeleteData(survey.get('path'));
    } else {
        return false; // need survey and survey path, otherwise prevent delete
    }
  },

  /* retrieve the list of permissions associated with the currently
    active form */
  currentFormPermissions: function() {
    var currentForm = FLOW.selectedControl.get('selectedSurvey');
    var currentUserPermissions = FLOW.currentUser.get('pathPermissions');
    var formPermissions = [];

    if (!currentForm || !currentUserPermissions) {
      return [];
    }

    var ancestorIds = currentForm.get('ancestorIds');
    if (!ancestorIds) {
      return [];
    }

    var i;
    for(i = 0; i < ancestorIds.length; i++){
      if (ancestorIds[i] in currentUserPermissions) {
        currentUserPermissions[ancestorIds[i]].forEach(function(item){
          formPermissions.push(item);
        });
      }
    }

    return formPermissions;

  }.property('FLOW.selectedControl.selectedSurvey'),
});


FLOW.questionGroupControl = Ember.ArrayController.create({
  sortProperties: ['order'],
  sortAscending: true,
  content: null,

  setFilteredContent: function () {
    var sId;
    if (FLOW.selectedControl.get('selectedSurvey')) {
      if (!Ember.empty(FLOW.selectedControl.selectedSurvey.get('keyId'))) {
        sId = FLOW.selectedControl.selectedSurvey.get('keyId');
        this.set('content', FLOW.store.filter(FLOW.QuestionGroup, function (item) {
          return item.get('surveyId') == sId;
        }));
      } else {
        // this happens when we have created a new survey, which has no id yet
        this.set('content', null);
      }
    }
  },

  populate: function () {
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      var id = FLOW.selectedControl.selectedSurvey.get('keyId');
      FLOW.store.findQuery(FLOW.QuestionGroup, {
        surveyId: id
      });
    }
    this.setFilteredContent();
  }.observes('FLOW.selectedControl.selectedSurvey'),

  getQuestionGroup: function (id) {
	  FLOW.store.findQuery(FLOW.QuestionGroup,{
		  questionGroupId: id
	  });
  },

  // true if all items have been saved
  // used in models.js
  allRecordsSaved: function () {
    var allSaved = true;
    if (Ember.none(this.get('content'))) {
      return true;
    } else {
      this.get('content').forEach(function (item) {
        if (item.get('isSaving')) {
          allSaved = false;
        }
      });
      return allSaved;
    }
  }.property('content.@each.isSaving'),

  // execute group delete
  deleteQuestionGroup: function (questionGroupId) {
    var questionGroup, sId, qgOrder;
    sId = FLOW.selectedControl.selectedSurvey.get('keyId');
    questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);
    qgOrder = questionGroup.get('order');

    questionGroup.deleteRecord();

    // reorder the rest of the question groups
    this.reorderQuestionGroups(sId, qgOrder, "decrement");
    this.submitBulkQuestionGroupsReorder(sId);

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
  },

  reorderQuestionGroups: function (surveyId, reorderPoint, reorderOperation) {
    var questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, function (item) {
      return item.get('surveyId') == surveyId;
    });

    // move items up to make space
    questionGroupsInSurvey.forEach(function (item) {
      if (reorderOperation == "increment") {
        if (item.get('order') > reorderPoint) {
          item.set('order', item.get('order') + 1);
        }
      } else if (reorderOperation == "decrement") {
        if (item.get('order') > reorderPoint) {
          item.set('order', item.get('order') - 1);
        }
      }
    });
  },

  submitBulkQuestionGroupsReorder: function (surveyId) {
    FLOW.questionControl.set('bulkCommit', true);
    var questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, function (item) {
      return item.get('surveyId') == surveyId;
    });
    // restore order in case the order has gone haywire
    FLOW.questionControl.restoreOrder(questionGroupsInSurvey);
    FLOW.store.commit();
    FLOW.store.adapter.set('bulkCommit', false);
    FLOW.questionControl.set('bulkCommit', false);
  }
});


FLOW.questionControl = Ember.ArrayController.create({
  content: null,
  OPTIONcontent: null,
  earlierOptionQuestions: null,
  QGcontent: null,
  filterContent: null,
  sortProperties: ['order'],
  sortAscending: true,
  preflightQId: null,
  bulkCommit: false,

  populateAllQuestions: function () {
    var sId;
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('content', FLOW.store.findQuery(FLOW.Question, {
        surveyId: sId
      }));
    }
  }.observes('FLOW.selectedControl.selectedSurvey'),

  populateQuestionGroupQuestions: function (qgId) {
        this.set('content', FLOW.store.findQuery(FLOW.Question, {
          questionGroupId: qgId
        }));
    },

  // used for surveyInstances in data edit popup
  doSurveyIdQuery: function (surveyId) {
    this.set('content', FLOW.store.findQuery(FLOW.Question, {
      surveyId: surveyId
    }));
  },

  restoreOrder: function (groups) {
    var temp, i;
    // sort them and renumber them according to logical numbering
    temp = groups.toArray();
    temp.sort(function(a,b) {
    	return a.get('order') - b.get('order');
    });
    i = 1;
    temp.forEach(function(item){
      item.set('order',i);
      i++;
    });
  },

  deleteQuestion: function (questionId) {
    var question, qgId, qOrder;
    question = FLOW.store.find(FLOW.Question, questionId);
    qgId = question.get('questionGroupId');
    qOrder = question.get('order');
    question.deleteRecord();

    //reorder the rest of the questions
    this.reorderQuestions(qgId, qOrder, "decrement");
    this.submitBulkQuestionsReorder([qgId]);

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
  },

  allQuestionsFilter: function () {
    var sId;
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('filterContent', FLOW.store.filter(FLOW.Question, function (item) {
        return item.get('surveyId') == sId;
      }));
    } else {
      this.set('filterContent', null);
    }
  }.observes('FLOW.selectedControl.selectedSurvey'),

  setQGcontent: function () {
    if (FLOW.selectedControl.get('selectedQuestionGroup') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      var qId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      this.set('content', FLOW.store.filter(FLOW.Question, function (item) {
        return item.get('questionGroupId') == qId;
      }));
    }
  }.observes('FLOW.selectedControl.selectedQuestionGroup'),

  geoshapeContent: function() {
    var selectedSurvey = FLOW.selectedControl.get('selectedSurvey');
    var surveyId = selectedSurvey ? selectedSurvey.get('keyId') : null;
    return FLOW.store.filter(FLOW.Question, function (question) {
      return question.get('type') === 'GEOSHAPE' && surveyId === question.get('surveyId');
    });
  }.property('content'),

  downloadOptionQuestions: function (surveyId) {
	  this.set('OPTIONcontent', FLOW.store.findQuery(FLOW.Question, {
	     surveyId: surveyId,
	      optionQuestionsOnly:true
	  }));
  },

  // used for display of dependencies: a question can only be dependent on earlier questions
  setEarlierOptionQuestions: function () {
    if (!Ember.none(FLOW.selectedControl.get('selectedQuestion')) && !Ember.none(FLOW.selectedControl.get('selectedQuestionGroup'))) {
      var optionQuestionList, sId, questionGroupOrder, qgOrder, qg, questionOrder, questionGroupId;
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      questionGroupId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      questionGroupOrder = FLOW.selectedControl.selectedQuestionGroup.get('order');
      questionOrder = FLOW.selectedControl.selectedQuestion.get('order');
      optionQuestionList = FLOW.store.filter(FLOW.Question, function (item) {
        qg = FLOW.store.find(FLOW.QuestionGroup, item.get('questionGroupId'));
        // no dependencies from non-repeat to repeat groups
        if (qg.get('keyId') != questionGroupId && qg.get('repeatable')) {
          return false;
        }
        qgOrder = qg.get('order');
        if (!(item.get('type') == 'OPTION' && item.get('surveyId') == sId)) return false;
        if (qgOrder > questionGroupOrder) {
          return false;
        }
        if (qgOrder < questionGroupOrder) {
          return true;
        }
        // when we arrive there qgOrder = questionGroupOrder, so we have to check question order
        return item.get('order') < questionOrder;
      });

      this.set('earlierOptionQuestions', optionQuestionList);
    }
  }.observes('FLOW.selectedControl.selectedQuestion'),

  reorderQuestions: function (qgId, reorderPoint, reorderOperation) {
    var questionsInGroup = FLOW.store.filter(FLOW.Question, function (item) {
      return item.get('questionGroupId') == qgId;
    });

    // move items up to make space
    questionsInGroup.forEach(function (item) {
      if (reorderOperation == "increment") {
        if (item.get('order') > reorderPoint) {
          item.set('order', item.get('order') + 1);
        }
      } else if (reorderOperation == "decrement") {
        if (item.get('order') > reorderPoint) {
          item.set('order', item.get('order') - 1);
        }
      }
    });
  },

  submitBulkQuestionsReorder: function (qgIds) {
    this.set('bulkCommit', true);
    for (var i=0; i>qgIds.length; i++) {
      var questionsInGroup = FLOW.store.filter(FLOW.Question, function (item) {
        return item.get('questionGroupId') == qgIds[i];
      });
      // restore order in case the order has gone haywire
      FLOW.questionControl.restoreOrder(questionsInGroup);
    }
    FLOW.store.commit();
    FLOW.store.adapter.set('bulkCommit', false);
    this.set('bulkCommit', false);
  },


  // true if all items have been saved
  // used in models.js
  allRecordsSaved: function () {
    var allSaved = true;
    FLOW.questionControl.get('content').forEach(function (item) {
      if (item.get('isSaving')) {
        allSaved = false;
      }
    });
    return allSaved;
  }.property('content.@each.isSaving')
});

/*
 *  Note: This controller is for the option list for a question's dependencies
 */
FLOW.optionListControl = Ember.ArrayController.create({
  content: []
});

/*
 *  Controller for the list of options attached to an option question
 *
 */
FLOW.questionOptionsControl = Ember.ArrayController.create({
  content: null,
  questionId: null,

  /*
   *  Add two empty option objects to the options list.  This is used
   *  as a default setup for new option questions
   */
  loadDefaultOptions: function () {
    var c = this.content, defaultLength = 2;
    if (c && c.get('length') === 0) {
      while (defaultLength > 0) {
        c.addObject(Ember.Object.create({
          code: null,
          text: null,
          order: c.get('length') + 1,
          questionId: this.get('questionId'),
        }));
        defaultLength--;
      }
    }
  },

  /*
   *  Add a new option object to the content of this controller.  The object
   *  is not persisted to the data store.
   */
  addOption: function() {
    var c = this.content;
    c.addObject(Ember.Object.create({
        code: null,
        text: null,
        order: c.get('length') + 1,
        questionId: this.get('questionId'),
    }));
  },

  /*
   *  Persist all the newly added options to the data store.
   *  Options with empty code and empty text fields are dropped
   *  from the list.  If they were already persisted in the datastore
   *  they are deleted
   *
   */
  persistOptions: function () {
    var options = this.content, blankOptions = [];
    // remove blank options
    options.forEach(function (option) {
      var code = option.get('code') && option.get('code').trim();
      var text = option.get('text') && option.get('text').trim();
      if (!code && !text) {
        blankOptions.push(option);
        if (option.get('keyId')) {
          option.deleteRecord();
        }
      }
    });
    options.removeObjects(blankOptions);

    // reset ordering and persist
    options.forEach(function (option, index) {
      var code = option.get('code') && option.get('code').trim();
      var text = option.get('text') && option.get('text').trim();
      if (!code) {
        option.set('code', null); // do not send empty string as code
      } else {
        option.set('code', code);
      }

      // trimmed whitespace
      option.set('text', text);
      option.set('order', index);
      if (!option.get('keyId')) {
        FLOW.store.createRecord(FLOW.QuestionOption, option);
      }
    });
  },

  /*
   *  Remove an option from the list of options.
   *
   */
  deleteOption: function(event) {
    var c = this.content, option = event.view.content;
    c.removeObject(option);

    if (option.get('keyId')) { // clear persisted versions
      option.deleteRecord();
    }
  },

  /*
   *  Validate all code options and if there is invalid input
   *  return an error message.  Valid input returns null
   */
  validateOptions: function () {
    var options = this.content, error;

    if (!options) {
      return null;
    }

    error = this.validateAllTextFilled();
    if (error && error.trim().length > 0) {
      return Ember.String.htmlSafe(error);
    }

    error = this.validateAllCodesFilled();
    if (error && error.trim().length > 0) {
      return Ember.String.htmlSafe(error);
    }

    error = this.validateDuplicateCodes();
    if (error && error.trim().length > 0) {
      return Ember.String.htmlSafe(error);
    }

    error = this.validateDuplicateText();
    if (error && error.trim().length > 0) {
      return Ember.String.htmlSafe(error);
    }

    error = this.validateDisallowedCharacters();
    if (error && error.trim().length > 0) {
      return Ember.String.htmlSafe(error);
    }
    return null;
  },

  /*
   *  Return an error string of any text options are left blank
   */
  validateAllTextFilled: function () {
    var options = this.content, error = '';

    options.forEach(function (option) {
      // only take into account options with no text but with text filled in
      if (!option.get('text') || option.get('text').trim().length === 0) {
        if(option.get('code') && option.get('code').trim()) {
          error += "<li>" + option.get('code').trim() + "</li>"
        }
      }
    });

    if (error) {
      error = '<ul>' + error + '</ul>';
      error = Ember.String.loc('_missing_option_text') + "\n" + error;
      return error;
    }
    return null;
  },

  /*
   * Return an error string if codes are partially filled in
   */
  validateAllCodesFilled: function () {
    var options = this.content, error = '', hasCodes;

    options.forEach(function (option) {
      // only take into account options with text to be able to give error dialog
      if (option.get('text') && option.get('text').trim()) {
        if(option.get('code') && option.get('code').trim()) {
          hasCodes = true;
        } else {
          error += "<li>" + option.get('text').trim() + "</li>"
        }
      }
    });

    if (hasCodes && error) {
      error = '<ul>' + error + '</ul>';
      error = Ember.String.loc('_missing_option_codes') + "\n" + error;
      return error;
    }
    return null;
  },

  /*
   *  Check for duplicate codes in the created options
   */
  validateDuplicateCodes: function () {
    var options = this.content, error = '';

    var uniqCodes = [];
    options.forEach(function (option) {
      if (option.get('code') && option.get('code').trim()){
        if(uniqCodes.indexOf(option.get('code').trim()) > -1) {
          error += '<li>' + option.get('code').trim() + '</li>'
        } else {
          uniqCodes.push(option.get('code').trim());
        }
      }
    });

    if (error) {
      error = '<ul>' + error + '</ul>';
      error = Ember.String.loc('_duplicate_option_codes') + "\n" + error;
      return error;
    }

    return null;
  },

  /*
   *  Check for duplicate texts in the created options
   */
  validateDuplicateText: function () {
    var options = this.content, error = '';

    var uniqText = [];
    options.forEach(function (option) {
      if (option.get('text') && option.get('text').trim()){
        if(uniqText.indexOf(option.get('text').trim()) > -1) {
          error += '<li>' + option.get('text').trim() + '</li>'
        } else {
          uniqText.push(option.get('text').trim());
        }
      }
    });

    if (error) {
      error = '<ul>' + error + '</ul>';
      error = Ember.String.loc('_duplicate_option_text') + "\n" + error;
      return error;
    }

    return null;
  },

  /*
   *  Check for disallowed xters in option codes
   */
  validateDisallowedCharacters: function () {
    var options = this.content, error = '';

    var reservedCode = [];
    options.forEach(function (option) {
      if (option.get('code') && option.get('code').trim()){
        if(!option.get('code').trim().match(/^[A-Za-z0-9_\-]*$/)) {
          error += '<li>' + option.get('code').trim() + '</li>'
        }

        if (option.get('code').trim() === "OTHER") {
          reservedCode.push(option.get('code').trim());
        }
      }
    });

    if (error) {
      error = '<ul>' + error + '</ul>';
      error = Ember.String.loc('_disallowed_xters_in_code') + "\n" + error;
      return error;
    }

    if (reservedCode.length) {
      error = Ember.String.loc('_reserved_code');
      return error;
    }

    return null;
  },
});

FLOW.previewControl = Ember.ArrayController.create({
  changed: false,
  showPreviewPopup: false,
  // associative array for answers in the preview
  answers: {}
});


FLOW.notificationControl = Ember.ArrayController.create({
  content: null,
  filterContent: null,
  sortProperties: ['notificationDestination'],
  sortAscending: true,

  populate: function () {
    var id;
    if (FLOW.selectedControl.get('selectedSurvey')) {
      id = FLOW.selectedControl.selectedSurvey.get('keyId');
      FLOW.store.findQuery(FLOW.NotificationSubscription, {
        surveyId: id
      });
    }
  },

  doFilterContent: function () {
    var sId;
    if (FLOW.selectedControl.get('selectedSurvey') && FLOW.selectedControl.selectedSurvey.get('keyId') > 0) {
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');
      this.set('content', FLOW.store.filter(FLOW.NotificationSubscription, function (item) {
        return item.get('entityId') == sId;
      }));
    }
  }.observes('FLOW.selectedControl.selectedSurvey')
});


FLOW.translationControl = Ember.ArrayController.create({
  itemArray: [],
  itemDict: {},
  translations: [],
  isoLangs: null,
  questionGroups: null,
  currentTranslation: null,
  currentTranslationName: null,
  defaultLang: null,
  selectedLanguage: null,
  newSelected: false,
  noCurrentTrans: true,
  toBeDeletedTranslations: [],
  firstLoad: true,

  init: function () {
    this._super();
    this.createIsoLangs();
  },

  createIsoLangs: function () {
    var tempArray = [];
    for (var key in FLOW.isoLanguagesDict) {
      tempArray.push(Ember.Object.create({
        value: key,
        labelShort: FLOW.isoLanguagesDict[key].nativeName,
        labelLong: FLOW.isoLanguagesDict[key].nativeName + " - " + FLOW.isoLanguagesDict[key].name
      }));
    }
    this.set('isoLangs', tempArray);
  },

  blockInteraction: function () {
    return this.get('noCurrentTrans') || this.get('newSelected');
  }.property('noCurrentTrans', 'newSelected'),

  populate: function () {
    var id, questionGroupId, questionGroup;
    id = FLOW.selectedControl.selectedSurvey.get('keyId');
    questionGroupId = FLOW.questionGroupControl.get('arrangedContent')[0].get('keyId');
    questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);

    if (!Ember.none(questionGroup)){
    	FLOW.selectedControl.set('selectedQuestionGroup',questionGroup);
    }

    if (!Ember.none(id) && !Ember.none(questionGroupId)) {
      this.set('content', FLOW.store.findQuery(FLOW.Translation, {
        surveyId: id,
        questionGroupId: questionGroupId
      }));
      this.set('translations', []);
      this.set('newSelected', false);
      this.set('noCurrentTrans', true);
      this.set('selectedLanguage', null);
      this.set('currentTranslation', null);
      this.set('currentTranslationName', null);

      // this creates the internal structure that we use to display all the items for translation
      // the translation items are put in here when they arrive from the backend
      this.createItemList(id, questionGroupId);
      this.set('defaultLang', FLOW.isoLanguagesDict[FLOW.selectedControl.selectedSurvey.get('defaultLanguageCode')].name);
      this.set('firstLoad', true);
    }
  },

  loadQuestionGroup: function (questionGroupId) {
        var id;
	    id = FLOW.selectedControl.selectedSurvey.get('keyId');
	    questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);

	    if (!Ember.none(questionGroup)){
	    	FLOW.selectedControl.set('selectedQuestionGroup',questionGroup);
	    }

	    if (!Ember.none(id)) {}
	    this.set('content', FLOW.store.findQuery(FLOW.Translation, {
	      surveyId: id,
	      questionGroupId: questionGroupId
	    }));
	    this.set('firstLoad', false);

	    // this creates the internal structure that we use to display all the items for translation
	    // the translation items are put in here when they arrive from the backend
	    this.createItemList(id, questionGroupId);
  },

  //when the translations arrive, put them in the internal data structure
  initiateData: function () {
    if (this.get('firstLoad')){
	  if (this.get('content').content.length > 0) {
        this.determineAvailableTranslations();
        this.resetTranslationFields();
        if (this.get('translations').length > 0) {
          this.set('currentTranslation', this.get('translations')[0].value);
          this.set('currentTranslationName', this.get('translations')[0].label);
          this.putTranslationsInList();
          this.set('noCurrentTrans', false);
        } else {
          this.set('noCurrentTrans', true);
        }
      }
    } else {
      if (this.get('content').content.length > 0) {
    	  this.resetTranslationFields();
    	  this.putTranslationsInList();
      }
    }
  }.observes('content.isLoaded'),

  resetTranslationFields: function () {
    this.get('itemArray').forEach(function (item) {
      switch (item.get('type')) {
      case "S":
        item.set('surveyTextTrans', null);
        item.set('surveyTextTransId', null);
        item.set('sDescTextTrans', null);
        item.set('sDescTextTransId', null);
        break;

      case "QG":
        item.set('qgTextTrans', null);
        item.set('qgTextTransId', null);
        break;

      case "Q":
        item.set('qTextTrans', null);
        item.set('qTextTransId', null);
        item.set('qTipTextTrans', null);
        item.set('qTipTextTransId', null);
        break;

      case "QO":
        item.set('qoTextTrans', null);
        item.set('qoTextTransId', null);
        break;

      default:

      }
    });
  },

  // determine which languages are present in the translation objects,
  // so we can show the proper items
  determineAvailableTranslations: function () {
    var tempDict = {};
    this.get('content').forEach(function (item) {
      if (!Ember.none(item.get('langCode'))) {
        tempDict[item.get('langCode')] = item.get('langCode');
      }
    });
    for (var key in tempDict) this.translations.pushObject(Ember.Object.create({
      value: key,
      label: FLOW.isoLanguagesDict[key].name
    }));
  },

  cancelAddTranslation: function () {
    this.set('newSelected', false);
    this.set('selectedLanguage', null);
  },

  lockWhenNewLangChosen: function () {
    var selLang, found = false;
    if (!Ember.none(this.get('selectedLanguage'))) {
      if (FLOW.selectedControl.selectedSurvey.get('defaultLanguageCode') == this.selectedLanguage.get('value')) {
        // we can't select the same language as the default language
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_cant_select_lang'));
        FLOW.dialogControl.set('message', Ember.String.loc('_cant_select_lang_text'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
        this.set('selectedLanguage', null);
        this.set('newSelected', false);
        return;
      }
      selLang = this.selectedLanguage.get('value');
      this.get('translations').forEach(function (item) {
        found = found || selLang == item.value;
      });

      if (found) {
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_trans_already_present'));
        FLOW.dialogControl.set('message', Ember.String.loc('_trans_already_present_text'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
        this.set('selectedLanguage', null);
        this.set('newSelected', false);
        return;
      }
      this.set('newSelected', true);
    }
  }.observes('this.selectedLanguage'),

  addTranslation: function () {
    var found = false,
      newLang = null;
    newLang = this.get('selectedLanguage');
    if (!Ember.none(newLang)) {
      this.get('translations').forEach(function (item) {
        found = found || (newLang.value == item.value);
      });
      if (!found) {
        this.resetTranslationFields();
        this.translations.pushObject(Ember.Object.create({
          value: this.get('selectedLanguage').value,
          label: FLOW.isoLanguagesDict[this.get('selectedLanguage').value].name
        }));
        this.set('currentTranslation', this.get('selectedLanguage').value);
        this.set('currentTranslationName', FLOW.isoLanguagesDict[this.get('selectedLanguage').value].name);
        this.set('newSelected', false);
        this.set('noCurrentTrans', false);
      }
    }
  },

  switchTranslation: function (event) {
    if (event.context.value != this.get('currentTranslation')) {
      this.saveTranslations();
      this.resetTranslationFields();
      this.set('currentTranslation', event.context.value);
      this.set('currentTranslationName', FLOW.isoLanguagesDict[event.context.value].name);
      this.set('noCurrentTrans', false);
      this.putTranslationsInList();
    }
  },

  createItemList: function (id, questionGroupId) {
    var tempArray, tempHashDict, questionGroup, qgOrder;
    tempArray = [];
    tempHashDict = {};

    // put in survey stuff
    survey = FLOW.selectedControl.get('selectedSurvey');
    tempArray.push(Ember.Object.create({
      keyId: survey.get('keyId'),
      type: "S",
      order: 0,
      surveyText: survey.get('name'),
      sDescText: survey.get('description'),
      isSurvey: true
    }));

    // put in question group
    questionGroup = FLOW.store.find(FLOW.QuestionGroup, questionGroupId);
    if (!Ember.none(questionGroup)){
      tempArray.push(Ember.Object.create({
        keyId: questionGroup.get('keyId'),
        type: "QG",
        order: 1000000 * parseInt(questionGroup.get('order'), 10),
        displayOrder: questionGroup.get('order'),
        qgText: questionGroup.get('name'),
        isQG: true
      }));
    }
    // put in questions
    questions = FLOW.store.filter(FLOW.Question, function (item) {
      return item.get('questionGroupId') == questionGroupId;
    });
    questions.forEach(function (item) {
      questionGroup = FLOW.store.find(FLOW.QuestionGroup, item.get('questionGroupId'));
      qgOrder = parseInt(questionGroup.get('order'), 10);
      qId = item.get('keyId');

      tempArray.push(Ember.Object.create({
        keyId: item.get('keyId'),
        type: "Q",
        order: 1000000 * qgOrder + 1000 * parseInt(item.get('order'), 10),
        qText: item.get('text'),
        displayOrder: item.get('order'),
        qTipText: item.get('tip'),
        isQ: true,
        hasTooltip: !Ember.empty(item.get('tip'))
      }));
      // for each question, put in question options
      options = FLOW.store.filter(FLOW.QuestionOption, function (optionItem) {
        return optionItem.get('questionId') == qId;
      });

      qOrder = parseInt(item.get('order'), 10);
      options.forEach(function (item) {
        tempArray.push(Ember.Object.create({
          keyId: item.get('keyId'),
          type: "QO",
          order: 1000000 * qgOrder + 1000 * qOrder + parseInt(item.get('order'), 10),
          displayOrder: item.get('order'),
          qoText: item.get('text'),
          isQO: true
        }));
      });
    });

    // put all the items in the right order
    tempArray.sort(function (a, b) {
    	return a.get('order') - b.get('order');
    });

    i = 0;
    tempArray.forEach(function (item) {
      tempHashDict[item.get('type') + item.get('keyId')] = i;
      i++;
    });

    this.set('itemDict', tempHashDict);
    this.set('itemArray', tempArray);
  },

  // if deleteItem is true, the translation information is deleted
  putSingleTranslationInList: function (parentType, parentId, text, keyId, deleteItem) {
    var existingItemPos, itemText, itemKeyId;
    if (deleteItem) {
      itemText = text;
      itemKeyId = null;
    } else {
      itemText = text;
      itemKeyId = keyId;
    }
    switch (parentType) {
    case "SURVEY_NAME":
      existingItemPos = this.get('itemDict')["S" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('surveyTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('surveyTextTransId', itemKeyId);
      }
      break;

    case "SURVEY_DESC":
      existingItemPos = this.get('itemDict')["S" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('sDescTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('sDescTextTransId', itemKeyId);
      }
      break;

    case "QUESTION_GROUP_NAME":
      existingItemPos = this.get('itemDict')["QG" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('qgTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('qgTextTransId', itemKeyId);
      }
      break;

    case "QUESTION_TEXT":
      existingItemPos = this.get('itemDict')["Q" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('qTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('qTextTransId', itemKeyId);
      }
      break;

    case "QUESTION_TIP":
      existingItemPos = this.get('itemDict')["Q" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('qTipTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('qTipTextTransId', itemKeyId);
      }
      break;

    case "QUESTION_OPTION":
      existingItemPos = this.get('itemDict')["QO" + parentId];
      if (!Ember.none(existingItemPos)) {
        this.get('itemArray')[existingItemPos].set('qoTextTrans', itemText);
        this.get('itemArray')[existingItemPos].set('qoTextTransId', itemKeyId);
      }
      break;

    default:
    }
  },


  putTranslationsInList: function () {
    var currLang, _self;
    _self = this;
    currTrans = this.get('currentTranslation');
    // only proceed if we have a language selected
    if (!Ember.none(currTrans)) {
      // get the translations with the right surveyId and the right language code
      translations = FLOW.store.filter(FLOW.Translation, function (item) {
        return (item.get('surveyId') == FLOW.selectedControl.selectedSurvey.get('keyId') && item.get('langCode') == currTrans);
      });
      translations.forEach(function (item) {
        _self.putSingleTranslationInList(item.get('parentType'), item.get('parentId'), item.get('text'), item.get('keyId'), false);
      });
    }
  },

  // delete a translation record by its Id. Committing is done in saveTranslations method
  deleteRecord: function (transId) {
    var candidates, existingTrans;
    candidates = FLOW.store.filter(FLOW.Translation, function (item) {
      return item.get('keyId') == transId;
    });

    if (candidates.get('content').length > 0) {
      existingTrans = candidates.objectAt(0);
      existingTrans.deleteRecord();
    }
  },

  createUpdateOrDeleteRecord: function (surveyId, questionGroupId, type, parentId, origText, translationText, lan, transId, allowSideEffects) {
	  var changed = false;
	  if (!Ember.none(origText) && origText.length > 0) {
      // we have an original text
      if (!Ember.none(translationText) && translationText.length > 0) {
        // we have a translation text
        if (Ember.none(transId)) {
          // we don't have an existing translation, so create it
        	changed = true;
        	if (allowSideEffects){
        	  FLOW.store.createRecord(FLOW.Translation, {
                parentType: type,
                parentId: parentId,
                surveyId: surveyId,
                questionGroupId: questionGroupId,
                text: translationText,
                langCode: lan
              });
            }
        } else {
          // we have an existing translation, so update it, if the text has changed
          candidates = FLOW.store.filter(FLOW.Translation, function (item) {
            return item.get('keyId') == transId;
          });

          if (candidates.get('content').length > 0) {
        	 existingTrans = candidates.objectAt(0);
        	 // if the existing translation is different from the existing one, update it
        	 if (existingTrans.get('text') != translationText){
        		 changed = true;
        		 if(allowSideEffects){
            		existingTrans.set('text', translationText);
            	 }
        	 }
          }
        }
      } else {
        // we don't have a translation text. If there is an existing translation, delete it
        if (!Ember.none(transId)) {
          // add this id to the list of to be deleted items
          changed = true;
          if (allowSideEffects){
        	  this.toBeDeletedTranslations.pushObject(transId);
          }
        }
      }
    } else {
      // we don't have an original text. If there is an existing translation, delete it
      if (!Ember.none(transId)) {
        // add this to the list of to be deleted items
        changed = true;
    	if (allowSideEffects){
    	  this.toBeDeletedTranslations.pushObject(transId);
        }
      }
    }
	return changed;
  },

  // checks if unsaved translations are present, and if so, emits a warning
  unsavedTranslations: function () {
	  var type, parentId, lan, transId, _self, unsaved;
	  _self = this;
	  unsaved = false;
	  this.get('itemArray').forEach(function (item) {
		  type = item.type;
	      parentId = item.keyId;
	      surveyId = FLOW.selectedControl.selectedSurvey.get('keyId');
        if (!Ember.none(FLOW.selectedControl.get('selectedQuestionGroup'))) {
          questionGroupId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
        } else {
          questionGroupId = null;
        }
	      lan = _self.get('currentTranslation');
	      if (type == 'S') {
	        unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "SURVEY_NAME", parentId, item.surveyText, item.surveyTextTrans, lan, item.surveyTextTransId, false);
	        unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "SURVEY_DESC", parentId, item.sDescText, item.sDescTextTrans, lan, item.sDescTextTransId, false);
	      } else if (type == 'QG') {
	    	unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_GROUP_NAME", parentId, item.qgText, item.qgTextTrans, lan, item.qgTextTransId, false);
	      } else if (type == 'Q') {
	    	unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_TEXT", parentId, item.qText, item.qTextTrans, lan, item.qTextTransId, false);
	    	unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_TIP", parentId, item.qTipText, item.qTipTextTrans, lan, item.qTipTextTransId, false);
	      } else if (type == 'QO') {
	    	unsaved = unsaved || _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_OPTION", parentId, item.qoText, item.qoTextTrans, lan, item.qoTextTransId, false);
	      }
	  });
      if (unsaved){
    	FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_unsaved_translations_present'));
        FLOW.dialogControl.set('message', Ember.String.loc('_unsaved_translations_present_text'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
      }
      return unsaved;
  },

  // after saving is complete, records insert themselves back into the translation item list
  saveTranslations: function () {
    var type, parentId, lan, transId, _self;
    _self = this;
    FLOW.store.adapter.set('bulkCommit', true);
    this.get('itemArray').forEach(function (item) {
      type = item.type;
      parentId = item.keyId;
      surveyId = FLOW.selectedControl.selectedSurvey.get('keyId');
      if (!Ember.none(FLOW.selectedControl.get('selectedQuestionGroup'))) {
        questionGroupId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
      } else {
        questionGroupId = null;
      }
      lan = _self.get('currentTranslation');
      if (type == 'S') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "SURVEY_NAME", parentId, item.surveyText, item.surveyTextTrans, lan, item.surveyTextTransId, true);
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "SURVEY_DESC", parentId, item.sDescText, item.sDescTextTrans, lan, item.sDescTextTransId, true);
      } else if (type == 'QG') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_GROUP_NAME", parentId, item.qgText, item.qgTextTrans, lan, item.qgTextTransId, true);
      } else if (type == 'Q') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_TEXT", parentId, item.qText, item.qTextTrans, lan, item.qTextTransId, true);
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_TIP", parentId, item.qTipText, item.qTipTextTrans, lan, item.qTipTextTransId, true);
      } else if (type == 'QO') {
        _self.createUpdateOrDeleteRecord(surveyId, questionGroupId, "QUESTION_OPTION", parentId, item.qoText, item.qoTextTrans, lan, item.qoTextTransId, true);
      }
    });
    FLOW.store.commit();
    FLOW.store.adapter.set('bulkCommit', false);

    // delete items individually, as a body in a DELETE request is not accepted by GAE
    this.get('toBeDeletedTranslations').forEach(function (item) {
      _self.deleteRecord(item);
    });

    // make survey unpublished
    survey = FLOW.store.find(FLOW.Survey,surveyId);
    if (!Ember.empty(survey)){
        survey.set('status','NOT_PUBLISHED');
    }
    FLOW.store.commit();
    this.set('toBeDeletedTranslations', []);
  }
});

FLOW.CaddisflyResourceController = Ember.ArrayController.extend({
    sortProperties: ['name'],
    sortAscending: true,
    caddisflyTestsFileUrl: FLOW.Env.caddisflyTestsFileUrl,
    testsFileLoaded: false,

    load: function () {
        var self = this;
        $.getJSON(this.get('caddisflyTestsFileUrl'), function (caddisflyTestsFileContent) {
            self.set('content', self.parseCaddisflyTestsFile(caddisflyTestsFileContent));
            self.set('testsFileLoaded', true);
        }).fail(function () {
            self.set('content', []);
        });
    },

    parseCaddisflyTestsFile: function (caddisflyTestsFileContent) {
        var caddisflyTests = Ember.A();
        caddisflyTestsFileContent.tests.forEach(function (test) {
            caddisflyTests.push(FLOW.CaddisflyTestDefinition.create({
                "name": test.name,
                "brand": test.brand,
                "uuid": test.uuid,
            }));
        });

        return caddisflyTests;
    },
});
});

loader.register('akvo-flow/controllers/survey-selection', function(require) {

FLOW.SurveySelection = Ember.ObjectController.extend({
  surveyGroups: null,
  selectionFilter: null,

  populate: function() {
    selectionFilter = this.get('selectionFilter');
    if(selectionFilter) {
        this.surveyGroups = FLOW.store.filter(FLOW.SurveyGroup, selectionFilter);
    } else {
        this.surveyGroups = FLOW.store.filter(FLOW.SurveyGroup);
    }
  },

  init: function() {
    this._super();
    this.populate();
  },

  getByParentId: function(parentId, monitoringGroupsOnly) {

    return this.get('surveyGroups').filter(function(sg) {
      if (monitoringGroupsOnly) {
        return sg.get('parentId') === parentId &&
          (sg.get('monitoringGroup') || sg.get('projectType') === 'PROJECT_FOLDER');
      } else {
        return sg.get('parentId') === parentId;
      }
    }).sort(function (survey1, survey2) {
      var s1 = survey1.get('name') || "";
      var s2 = survey2.get('name') || "";

      return s1.toLocaleLowerCase().localeCompare(
        s2.toLocaleLowerCase());
    });
  },

  getSurvey: function(keyId) {
    var surveyGroups = this.get('surveyGroups').filter(function(sg) {
      return sg.get('keyId') === keyId;
    });

    return surveyGroups[0];
  },

  isSurvey: function(keyId) {
    return this.getSurvey(keyId).get('projectType') === 'PROJECT';
  },
});

});

loader.register('akvo-flow/controllers/user-controllers', function(require) {
FLOW.UserListController = Ember.ArrayController.extend({
  sortProperties: null,
  sortAscending: true,
  content: null,
  currentUser: null,
  dataCleaningPaths: null,

  setFilteredContent: function () {
    this.set('content', FLOW.store.findAll(FLOW.User));
  },

  // load all Survey Groups
  populate: function () {
    this.setFilteredContent();
    this.set('sortProperties', ['userName']);
    this.set('sortAscending', true);
  },

  getSortInfo: function () {
    this.set('sortProperties', FLOW.tableColumnControl.get('sortProperties'));
    this.set('sortAscending', FLOW.tableColumnControl.get('sortAscending'));
    this.set('selected', FLOW.tableColumnControl.get('selected'));
  },

  /* return all the ancestor paths for a given path */
  ancestorPaths: function(pathString) {
    if(!pathString) {
        return [];
    }

    var ancestors = [];
    while(pathString) {
        ancestors.push(pathString);
        pathString = pathString.slice(0, pathString.lastIndexOf("/"));
    }
    ancestors.push("/"); // add the root level folder to ancestors list
    return ancestors;
  },
});

});

loader.register('akvo-flow/core-common', function(require) {
require('akvo-flow/templ-common');
// Ember.LOG_BINDINGS = true;
// Create the application
window.FLOW = Ember.Application.create({
  VERSION: '0.0.1'
});

/* Generic FLOW view that also handles language rerenders */
FLOW.View = Ember.View.extend({});

});

loader.register('akvo-flow/main-public', function(require) {
require('akvo-flow/models/FLOWrest-adapter-v2-common');
require('akvo-flow/models/models-public');
require('akvo-flow/flowenv');
require('akvo-flow/controllers/controllers-public');
require('akvo-flow/views/views-public');
require('akvo-flow/router/router-public');

FLOW.initialize();

});

loader.register('akvo-flow/main', function(require) {
require('akvo-flow/models/FLOWrest-adapter-v2-common');
require('akvo-flow/models/models');
require('akvo-flow/flowenv');
require('akvo-flow/controllers/controllers');
require('akvo-flow/views/views');
require('akvo-flow/router/router');

FLOW.initialize();

});

loader.register('akvo-flow/models/FLOWrest-adapter-v2-common', function(require) {
/*global DS*/
var get = Ember.get,
  set = Ember.set;

DS.FLOWRESTAdapter = DS.RESTAdapter.extend({
  serializer: DS.RESTSerializer.extend({
    primaryKey: function (type) {
      return "keyId";
    },
    keyForAttributeName: function (type, name) {
      return name;
    }
  }),

  sideload: function (store, type, json, root) {
    var msg, status, metaObj;
    this._super(store, type, json, root);

    this.setQueryCursor(type, json);

    // only change metaControl info if there is actual meta info in the server response
    // and if it does not come from a delete action. We detect this by looking if num == null
    metaObj = this.extractMeta(json);
    if (metaObj && !Ember.none(metaObj.message)) {

      if (type == FLOW.SurveyInstance
          || type == FLOW.SurveyedLocale
          && !Ember.none(this.extractMeta(json).num)) {
        FLOW.metaControl.set(type == FLOW.SurveyInstance ? 'numSILoaded' : 'numSLLoaded', this.extractMeta(json).num);
        FLOW.metaControl.set('since', this.extractMeta(json).since);
        FLOW.metaControl.set('num', this.extractMeta(json).num);
        FLOW.metaControl.set('cursorType', type);
      }
      msg = this.extractMeta(json).message;
      status = this.extractMeta(json).status;
      keyId = this.extractMeta(json).keyId;

      if (msg.indexOf('_') === 0) { // Response is a translatable message
        msg = Ember.String.loc(msg);
      }
      FLOW.metaControl.set('message', msg);
      FLOW.metaControl.set('status', status);
      FLOW.metaControl.set('keyId', keyId);
      FLOW.savingMessageControl.numLoadingChange(-1);
      FLOW.savingMessageControl.set('areSavingBool', false);

      if (status === 'preflight-delete-question') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.questionControl.deleteQuestion(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_question'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_question_text'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
        }
        return;
      }

      if (status === 'preflight-delete-questiongroup') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.questionGroupControl.deleteQuestionGroup(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_questiongroup'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_questiongroup_text'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
        }
        return;
      }

      if (status === 'preflight-delete-survey') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.surveyControl.deleteSurvey(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_survey'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_survey_text'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
        }
        return;
      }

      if (status === 'preflight-delete-surveygroup') {
        if (msg === 'can_delete') {
          // do deletion
          FLOW.surveyGroupControl.deleteSurveyGroup(keyId);
        } else {
          FLOW.dialogControl.set('activeAction', 'ignore');
          FLOW.dialogControl.set('header', Ember.String.loc('_cannot_delete_surveygroup'));
          FLOW.dialogControl.set('message', Ember.String.loc('_cannot_delete_surveygroup_text'));
          FLOW.dialogControl.set('showCANCEL', false);
          FLOW.dialogControl.set('showDialog', true);
        }
        return;
      }

      if (this.extractMeta(json).status === 'failed' || FLOW.metaControl.get('message') !== '') {
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', '' /*Ember.String.loc('_action_failed')*/ ); //FIXME
        FLOW.dialogControl.set('message', FLOW.metaControl.get('message'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
      }
    }
  },

  /*  Process the cursor returned by the query. The cursor is used for pagination requests
      and is based on the type of entities queried */
  setQueryCursor: function(type, json) {
    var cursorArray, cursorStart, cursorIndex;
    if (type === FLOW.SurveyedLocale) {
      cursorArray = FLOW.router.surveyedLocaleController.get('sinceArray');
    } else if (type === FLOW.SurveyInstance) {
      cursorArray = FLOW.surveyInstanceControl.get('sinceArray');
    } else {
      return;
    }

    cursorStart = this.extractSince(json);
    if (!cursorStart) {
      return;
    }

    cursorIndex = cursorArray.indexOf(cursorStart);
    if (cursorIndex === -1) {
      cursorArray.pushObject(cursorStart);
    } else {
      // drop all cursors after the current one
      cursorArray.splice(cursorIndex + 1, cursorArray.length);
    }

    if (type === FLOW.SurveyedLocale) {
      FLOW.router.surveyedLocaleController.set('sinceArray', cursorArray);
    } else if (type === FLOW.SurveyInstance) {
      FLOW.surveyInstanceControl.set('sinceArray', cursorArray);
    }
  },

  ajax: function (url, type, hash) {
    if (type === 'GET' && url.indexOf('rest/survey_groups/0') >= 0) {
      // Don't fetch the root folder. It doesn't exist.
      return;
    }

    this._super(url, type, hash);
    if (type == "GET") {
      if (url.indexOf('rest/survey_groups') >= 0) {
        FLOW.projectControl.set('isLoading', true);
      }
      FLOW.savingMessageControl.numLoadingChange(1);
    }
  },

  didFindRecord: function (store, type, json, id) {
    this._super(store, type, json, id);
    if (type === FLOW.SurveyGroup) {
      FLOW.projectControl.set('isLoading', false);
    }
    FLOW.savingMessageControl.numLoadingChange(-1);
  },

  didFindAll: function (store, type, json) {
    if (type === FLOW.SurveyGroup) {
      FLOW.projectControl.set('isLoading', false);
    }
    FLOW.savingMessageControl.numLoadingChange(-1);
    this._super(store, type, json);
  },

  didFindQuery: function (store, type, json, recordArray) {
    this._super(store, type, json, recordArray);
    if (type === FLOW.SurveyGroup) {
      FLOW.projectControl.set('isLoading', false);
    }
    FLOW.savingMessageControl.numLoadingChange(-1);
  },

  // adapted from standard ember rest_adapter
  // includes 'bulk' in the POST call, to allign
  // with updateRecords and deleteRecords behaviour.
  createRecords: function (store, type, records) {
    //do not bulk commit when creating questions and question groups
    if (FLOW.questionControl.get('bulkCommit')) {
      this.set('bulkCommit', false);
    }

    if (get(this, 'bulkCommit') === false) {
      return this._super(store, type, records);
    }

    var root = this.rootForType(type),
      plural = this.pluralize(root);

    var data = {};
    data[plural] = [];
    records.forEach(function (record) {
      data[plural].push(this.serialize(record, {
        includeId: true
      }));
    }, this);

    this.ajax(this.buildURL(root, 'bulk'), "POST", {
      data: data,
      context: this,
      success: function (json) {
        this.didCreateRecords(store, type, records, json);
      }
    });
  },


  updateRecords: function(store, type, records) {
    //if updating questions and question groups ordering, enable bulkCommit
    if (FLOW.questionControl.get('bulkCommit')) {
      this.set('bulkCommit', true);
    }
    this._super(store, type, records);
  },

  deleteRecords: function(store, type, records) {
    //do not bulk commit when deleting questions and question groups
    if (FLOW.questionControl.get('bulkCommit')) {
      this.set('bulkCommit', false);
    }
    this._super(store, type, records);
  }
});

});

loader.register('akvo-flow/models/fixtures', function(require) {
FLOW.SurveyGroup.FIXTURES = [{
  id: 1,
  keyId: 1,
  code: 'Urban sanitation surveys'
}, {
  id: 2,
  keyId: 2,
  code: 'Elise Surveys'
}, {
  id: 3,
  keyId: 3,
  code: 'Test Survey group'
}, {
  id: 4,
  keyId: 4,
  code: 'Upande - SNVVERMIS'
}, {
  id: 5,
  keyId: 5,
  code: '1 Akvo test surveys'
}, {
  id: 6,
  keyId: 6,
  code: '2 Akvo test surveys'
}, {
  id: 7,
  keyId: 7,
  code: '3 Akvo test surveys'
}, {
  id: 8,
  keyId: 8,
  code: '4 Akvo test surveys'
}, {
  id: 9,
  keyId: 9,
  code: '5 Akvo test surveys'
}, {
  id: 10,
  keyId: 11,
  code: '6 Akvo test surveys'
}, {
  id: 12,
  keyId: 12,
  code: '7 Akvo test surveys'
}, {
  id: 13,
  keyId: 14,
  code: '8 Akvo test surveys'
}, {
  id: 15,
  keyId: 15,
  code: '9 Akvo test surveys'
}, {
  id: 16,
  keyId: 16,
  code: '10 Akvo test surveys'
}, {
  id: 17,
  keyId: 17,
  code: '11 Akvo test surveys'
}, {
  id: 18,
  keyId: 18,
  code: '12 Akvo test surveys'
}, {
  id: 19,
  keyId: 19,
  code: '13 Akvo test surveys'
}, {
  id: 21,
  keyId: 21,
  code: '14 Akvo test surveys'
}, {
  id: 22,
  keyId: 22,
  code: '15 Akvo test surveys'
}, {
  id: 23,
  keyId: 23,
  code: '16 Akvo test surveys'
}, {
  id: 24,
  keyId: 24,
  code: '17 Akvo test surveys'
}, {
  id: 25,
  keyId: 25,
  code: '18 Akvo test surveys'
}, {
  id: 26,
  keyId: 26,
  code: '19 Akvo test surveys'
}, {
  id: 27,
  keyId: 27,
  code: '20 Akvo test surveys'
}, {
  id: 28,
  keyId: 28,
  code: '21 Akvo test surveys'
}, {
  id: 29,
  keyId: 29,
  code: '22 Akvo test surveys'
}, {
  id: 30,
  keyId: 30,
  code: '23 Akvo test surveys'
}, {
  id: 31,
  keyId: 31,
  code: '24 Akvo test surveys'
}, {
  id: 32,
  keyId: 32,
  code: '25 Akvo test surveys'
}];


FLOW.Survey.FIXTURES = [{
  id: 1,
  keyId: 1,
  code: 'Water point survey',
  name: 'Water point survey',
  surveyGroupId: 1,
  instanceCount: 62
}, {
  id: 2,
  keyId: 2,
  code: 'Sanitation survey',
  name: 'Sanitation survey',
  surveyGroupId: 1,
  instanceCount: 1400
}, {
  id: 3,
  keyId: 3,
  code: 'Baseline WASH',
  name: 'Baseline WASH',
  surveyGroupId: 1,
  instanceCount: 7
}, {
  id: 4,
  keyId: 4,
  code: 'Akvo RSR update',
  name: 'Akvo RSR update',
  surveyGroupId: 1,
  instanceCount: 787
}, {
  id: 5,
  keyId: 5,
  code: 'Akvo update',
  name: 'Akvo update',
  surveyGroupId: 1,
  instanceCount: 77
}, {
  id: 6,
  keyId: 6,
  code: 'Loics survey',
  name: 'Loics survey',
  surveyGroupId: 1,
  instanceCount: 7
}, {
  id: 7,
  keyId: 7,
  code: 'Farmer survey',
  name: 'Farmer survey',
  surveyGroupId: 1,
  instanceCount: 723
}, {
  id: 8,
  keyId: 8,
  code: 'Rabbit',
  name: 'Rabbit',
  surveyGroupId: 1,
  instanceCount: 3
}, {
  id: 9,
  keyId: 9,
  code: 'Rabbit II',
  name: 'Rabbit II',
  surveyGroupId: 1,
  instanceCount: 20000
}];


FLOW.QuestionGroup.FIXTURES = [{
    id: 1,
    keyId: 1,
    surveyId: 1,
    order: 1,
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu. ",
    code: 'Location',
    displayName: 'Location'

  }, {
    id: 2,
    keyId: 2,
    surveyId: 1,
    order: 2,
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
    code: 'Occupation',
    displayName: 'Occupation'

  }, {
    id: 3,
    keyId: 3,
    surveyId: 1,
    order: 3,
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
    code: 'Water system',
    displayName: 'Water system'

  }, {
    id: 4,
    keyId: 4,
    surveyId: 1,
    order: 4,
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
    code: 'Sanitation system',
    displayName: 'Sanitation system'

  }, {
    id: 5,
    keyId: 5,
    surveyId: 2,
    order: 5,
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
    code: 'Something else',
    displayName: 'Something else'

  }

];

FLOW.Question.FIXTURES = [{
  id: 1,
  keyId: 1,
  text: 'What is the name of the community?',
  displayName: 'What is the name of the community?',
  mandatory: false,
  order: 1,
  questionTypeString: 'freeText',
  questionSetId: 1
}, {
  id: 2,
  keyId: 2,
  text: 'What is your occupation?',
  displayName: 'What is your occupation?',
  mandatory: false,
  order: 2,
  questionTypeString: 'option',
  questionSetId: 1
}, {
  id: 3,
  keyId: 3,
  text: 'How much children do you have?',
  displayName: 'How much children do you have?',
  mandatory: false,
  order: 3,
  questionTypeString: 'number',
  questionSetId: 1
}, {
  id: 4,
  keyId: 4,
  text: 'Please take a geolocation',
  displayName: 'Please take a geolocation',
  mandatory: false,
  order: 4,
  questionTypeString: 'geoLoc',
  questionSetId: 1
}, {
  id: 5,
  keyId: 5,
  text: 'Please take a picture',
  displayName: 'Please take a picture',
  mandatory: false,
  order: 5,
  questionTypeString: 'photo',
  questionSetId: 1
}, {
  id: 6,
  keyId: 6,
  text: 'Please make a video',
  displayName: 'Please make a video',
  questionTypeString: 'video',
  order: 6,
  mandatory: false,
  questionSetId: 1
}, {
  id: 7,
  keyId: 7,
  text: 'What is the date today?',
  displayName: 'What is the date today?',
  questionTypeString: 'date',
  order: 7,
  mandatory: false,
  questionSetId: 1
}];

FLOW.QuestionOption.FIXTURES = [{
  id: 1,
  keyId: 1,
  text: 'teacher',
  questionId: 1
}, {
  id: 2,
  keyId: 2,
  text: 'cook',
  questionId: 1
}, {
  id: 3,
  keyId: 3,
  text: 'minister',
  questionId: 1
}, {
  id: 4,
  keyId: 4,
  text: 'programmer',
  questionId: 1
}];


FLOW.DeviceGroup.FIXTURES = [{
  id: 1,
  displayName: 'Malawi',
  code: 'malawi'
}, {
  id: 2,
  displayName: 'Bolivia',
  code: 'bolivia'
}];

FLOW.Device.FIXTURES = [{
  id: 1,
  keyId: 1,
  phoneNumber: "3f:d4:8f:2a:8c:9f",
  deviceIdentifier: "Keri phone 1",
  deviceGroup: "WFP general",
  lastUpdate: "21 May 2012 20:30:00",
  lastLocationBeaconTime: "22 May 2012 20:30:00",
  lastKnownLat: 23.132132321,
  lastKnownLong: 12.23232332
}, {
  id: 2,
  keyId: 2,
  phoneNumber: "2a:8c:9f:3f:d4:8f",
  deviceIdentifier: " Keri phone 2",
  deviceGroup: "WFP general",
  lastUpdate: "21 Apr 2012 20:30:00",
  lastLocationBeaconTime: "27 Feb 2012 20:30:00",
  lastKnownLat: 43.33434343,
  lastKnownLong: -5.32332343
}, {
  id: 3,
  keyId: 3,
  phoneNumber: "31648492710",
  deviceIdentifier: "Marks phone",
  deviceGroup: "WFP general",
  lastUpdate: "01 Sep 2012 20:30:00",
  lastLocationBeaconTime: "12 Aug 2012 20:30:00",
  lastKnownLat: 34.222334234,
  lastKnownLong: -7.44343434
}, {
  id: 4,
  keyId: 4,
  phoneNumber: "34029392833",
  deviceIdentifier: "WFP colombia-1",
  deviceGroup: "Colombia",
  lastUpdate: "21 Aug 2012 20:30:00",
  lastLocationBeaconTime: "04 Jan 2012 20:30:00",
  lastKnownLat: 2.334343434,
  lastKnownLong: -23.33433432
}, {
  id: 5,
  keyId: 5,
  phoneNumber: "3f:d4:8f:8b:8c:3e",
  deviceIdentifier: "WFP colombia 2",
  deviceGroup: "Colombia",
  lastUpdate: "12 Apr 2012 20:30:00",
  lastLocationBeaconTime: "31 Oct 2012 20:30:00",
  lastKnownLat: 8.55454435,
  lastKnownLong: 54.88399473
}, {
  id: 6,
  keyId: 6,
  phoneNumber: "2a:8c:9f:3f:d4:8f",
  deviceIdentifier: "WFP phone 3",
  deviceGroup: "Malawi",
  lastUpdate: "17 Jul 2012 20:30:00",
  lastLocationBeaconTime: "16 Jun 2012 20:30:00",
  lastKnownLat: 23.988332,
  lastKnownLong: -64.88399483
}, {
  id: 7,
  keyId: 7,
  phoneNumber: "3403928293",
  deviceIdentifier: "WFP phone 4",
  deviceGroup: "Malawi",
  lastUpdate: "11 Dec 2012 20:30:00",
  lastLocationBeaconTime: "14 Nov 2012 20:30:00",
  lastKnownLat: 23.3323432,
  lastKnownLong: 9.88873633
}];

FLOW.SurveyedLocale.FIXTURES = [{
  description: "Welkom in Amsterdam!",
  keyId: 1,
  latitude: 52.370216,
  longitude: 4.895168,
  typeMark: "WATER_POINT"
}, {
  description: "Welcome to London!",
  keyId: 2,
  latitude: 51.507335,
  longitude: -0.127683,
  typeMark: "WATER_POINT"
}, {
  description: "Välkommen till Stockholm!",
  keyId: 3,
  latitude: 59.32893,
  longitude: 18.06491,
  typeMark: "WATER_POINT"
}];

FLOW.Placemark.FIXTURES = [{
  longitude: 36.76034601,
  latitude: -1.29624521,
  collectionDate: 1328620272000,
  markType: "WATER_POINT",
  id: 530003
}, {
  longitude: 36.76052649,
  latitude: -1.29624207,
  collectionDate: 1331040590000,
  markType: "WATERPOINT",
  id: 545030
}, {
  longitude: 36.7545783327,
  latitude: -1.35175386504,
  collectionDate: 1331005669000,
  markType: "WATER_POINT",
  id: 549003
}, {
  longitude: 36.74724467,
  latitude: -1.26103461,
  collectionDate: 1333221136000,
  markType: "WATERPOINT",
  id: 606070
}, {
  longitude: 36.69691894,
  latitude: -1.25285542,
  collectionDate: 1333221922000,
  markType: "WATERPOINT",
  id: 609077
}, {
  longitude: 35.07498217,
  latitude: -0.15946829,
  collectionDate: 1334905070000,
  markType: "WATERPOINT",
  id: 732033
}, {
  longitude: 36.76023113,
  latitude: -1.29614013,
  collectionDate: 1335258461000,
  markType: "WATER_POINT",
  id: 761148
}, {
  longitude: 36.7905733168,
  latitude: -1.85040885561,
  collectionDate: 1339065449000,
  markType: "WATER_POINT",
  id: 950969
}, {
  longitude: 35.19765058,
  latitude: -0.15885514,
  collectionDate: 1339660634000,
  markType: "WATER_POINT",
  id: 990840
}, {
  longitude: 35.23715568,
  latitude: -0.16715051,
  collectionDate: 1340173295000,
  markType: "WATERPOINT",
  id: 1029003
}];

FLOW.PlacemarkDetail.FIXTURES = [{
  stringValue: "Community (CBO)",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "1. WP Ownership",
  placemarkId: 732033,
  id: 734238
}, {
  stringValue: "Functional ( in use)",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "1.Functional status",
  placemarkId: 732033,
  id: 734234
}, {
  stringValue: "Unsafe",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "2a. Quantitative in-field assessment",
  placemarkId: 732033,
  id: 735246
}, {
  stringValue: "Coloured (whitish- brownish)",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "2b.Qualitative in-field assessment",
  placemarkId: 732033,
  id: 735245
}, {
  stringValue: "Good- practically always",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "3. Reliability",
  placemarkId: 732033,
  id: 734235
}, {
  stringValue: "Yes",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "B.Sufficient for HHs",
  placemarkId: 732033,
  id: 732228
}, {
  stringValue: "Yes",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "C. Sufficient for livestock",
  placemarkId: 732033,
  id: 735242
}, {
  stringValue: "ahero youth",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "CBO, specify",
  placemarkId: 732033,
  id: 732222
}, {
  stringValue: "Unknown",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Committee in place:",
  placemarkId: 732033,
  id: 735249
}, {
  stringValue: "all",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Community, specify",
  placemarkId: 732033,
  id: 732224
}, {
  stringValue: "Name one",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Contact one",
  placemarkId: 732033,
  id: 735244
}, {
  stringValue: "40",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "D. HHs # served/day",
  placemarkId: 732033,
  id: 735241
}, {
  stringValue: "20/04/12",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Date Record",
  placemarkId: 732033,
  id: 728181
}, {
  stringValue: "No",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Do you have an SPA?",
  placemarkId: 732033,
  id: 734236
}, {
  stringValue: "community",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Funded by",
  placemarkId: 732033,
  id: 728179
}, {
  stringValue: "-0.16252854|35.07743752|1136.800048828125|7gs8a46",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "GPS reading",
  placemarkId: 732033,
  id: 732229
}, {
  stringValue: "alex",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Interviewee",
  placemarkId: 732033,
  id: 728178
}, {
  stringValue: "amara",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Interviewer",
  placemarkId: 732033,
  id: 728180
}, {
  stringValue: "ahero pan",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Name of source/water point",
  placemarkId: 732033,
  id: 728182
}, {
  stringValue: "onuonga",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Names one,specify",
  placemarkId: 732033,
  id: 735247
}, {
  stringValue: "No",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "O&M cost recovery",
  placemarkId: 732033,
  id: 732225
}, {
  stringValue: "LVNWSB",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Organisation",
  placemarkId: 732033,
  id: 728183
}, {
  stringValue: "No",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Part of the piped scheme",
  placemarkId: 732033,
  id: 734229
}, {
  stringValue: "/mnt/sdcard/fieldsurvey/surveyal/8/7/9/8/5/wfpPhoto18652367987985.jpg",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Photo",
  placemarkId: 732033,
  id: 732230
}, {
  stringValue: "No",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Specify none",
  placemarkId: 732033,
  id: 732223
}, {
  stringValue: "tura",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Sub-location",
  placemarkId: 732033,
  id: 734231
}, {
  stringValue: "< 1 hour",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Time",
  placemarkId: 732033,
  id: 732227
}, {
  stringValue: "Dam/Pan(runoff harvesting)",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Unimproved",
  placemarkId: 732033,
  id: 734233
}, {
  stringValue: "ksm/040",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "WP ID",
  placemarkId: 732033,
  id: 728177
}, {
  stringValue: "Community (technician) Name/NO",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "WP Maintenance",
  placemarkId: 732033,
  id: 735248
}, {
  stringValue: "Directly managed by the CBO",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "WP Management",
  placemarkId: 732033,
  id: 734237
}, {
  stringValue: "Year round",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Water Availability",
  placemarkId: 732033,
  id: 732226
}, {
  stringValue: "None",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Water Payment",
  placemarkId: 732033,
  id: 735250
}, {
  stringValue: "30",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Water consumption per ( in dry season)",
  placemarkId: 732033,
  id: 735243
}, {
  stringValue: "Unimproved",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Water source type",
  placemarkId: 732033,
  id: 734232
}, {
  stringValue: "No",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Within WSP",
  placemarkId: 732033,
  id: 734230
}, {
  stringValue: "2004",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Year Constructed",
  placemarkId: 732033,
  id: 732231
}];


FLOW.QuestionAnswer.FIXTURES = [{
  value: "Community (CBO)",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "1. WP Ownership",
  placemarkId: 732033,
  id: 734238
}, {
  value: "Functional ( in use)",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "1.Functional status",
  placemarkId: 732033,
  id: 734234
}, {
  value: "Unsafe",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "2a. Quantitative in-field assessment",
  placemarkId: 732033,
  id: 735246
}, {
  value: "Coloured (whitish- brownish)",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "2b.Qualitative in-field assessment",
  placemarkId: 732033,
  id: 735245
}, {
  value: "Good- practically always",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "3. Reliability",
  placemarkId: 732033,
  id: 734235
}, {
  value: "Yes",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "B.Sufficient for HHs",
  placemarkId: 732033,
  id: 732228
}, {
  value: "Yes",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "C. Sufficient for livestock",
  placemarkId: 732033,
  id: 735242
}, {
  value: "ahero youth",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "CBO, specify",
  placemarkId: 732033,
  id: 732222
}, {
  value: "Unknown",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Committee in place:",
  placemarkId: 732033,
  id: 735249
}, {
  value: "all",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Community, specify",
  placemarkId: 732033,
  id: 732224
}, {
  value: "Name one",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Contact one",
  placemarkId: 732033,
  id: 735244
}, {
  value: "40",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "D. HHs # served/day",
  placemarkId: 732033,
  id: 735241
}, {
  value: "20/04/12",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Date Record",
  placemarkId: 732033,
  id: 728181
}, {
  value: "No",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Do you have an SPA?",
  placemarkId: 732033,
  id: 734236
}, {
  value: "community",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Funded by",
  placemarkId: 732033,
  id: 728179
}, {
  value: "-0.16252854|35.07743752|1136.800048828125|7gs8a46",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "GPS reading",
  placemarkId: 732033,
  id: 732229
}, {
  value: "alex",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Interviewee",
  placemarkId: 732033,
  id: 728178
}, {
  value: "amara",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Interviewer",
  placemarkId: 732033,
  id: 728180
}, {
  value: "ahero pan",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Name of source/water point",
  placemarkId: 732033,
  id: 728182
}, {
  value: "onuonga",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Names one,specify",
  placemarkId: 732033,
  id: 735247
}, {
  value: "No",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "O&M cost recovery",
  placemarkId: 732033,
  id: 732225
}, {
  value: "LVNWSB",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Organisation",
  placemarkId: 732033,
  id: 728183
}, {
  value: "No",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Part of the piped scheme",
  placemarkId: 732033,
  id: 734229
}, {
  value: "/mnt/sdcard/fieldsurvey/surveyal/8/7/9/8/5/wfpPhoto18652367987985.jpg",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Photo",
  placemarkId: 732033,
  id: 732230
}, {
  value: "No",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Specify none",
  placemarkId: 732033,
  id: 732223
}, {
  value: "tura",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Sub-location",
  placemarkId: 732033,
  id: 734231
}, {
  value: "< 1 hour",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Time",
  placemarkId: 732033,
  id: 732227
}, {
  value: "Dam/Pan(runoff harvesting)",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Unimproved",
  placemarkId: 732033,
  id: 734233
}, {
  value: "ksm/040",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "WP ID",
  placemarkId: 732033,
  id: 728177
}, {
  value: "Community (technician) Name/NO",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "WP Maintenance",
  placemarkId: 732033,
  id: 735248
}, {
  value: "Directly managed by the CBO",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "WP Management",
  placemarkId: 732033,
  id: 734237
}, {
  value: "Year round",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Water Availability",
  placemarkId: 732033,
  id: 732226
}, {
  value: "None",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Water Payment",
  placemarkId: 732033,
  id: 735250
}, {
  value: "30",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Water consumption per ( in dry season)",
  placemarkId: 732033,
  id: 735243
}, {
  value: "Unimproved",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Water source type",
  placemarkId: 732033,
  id: 734232
}, {
  value: "No",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Within WSP",
  placemarkId: 732033,
  id: 734230
}, {
  value: "2004",
  collectionDate: 1334938302000,
  metricName: "Mars / Initial question du planteur",
  questionText: "Year Constructed",
  placemarkId: 732033,
  id: 732231
}];

FLOW.SurveyInstance.FIXTURES = [{
  submitterName: "Community (CBO)",
  collectionDate: 1334938302001,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "1. WP Ownership",
  placemarkId: 732033,
  id: 734238
}, {
  submitterName: "Functional ( in use)",
  collectionDate: 1334938302002,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "1.Functional status",
  placemarkId: 732033,
  id: 734234
}, {
  submitterName: "Unsafe",
  collectionDate: 1334938302003,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "2a. Quantitative in-field assessment",
  placemarkId: 732033,
  id: 735246
}, {
  submitterName: "Coloured (whitish- brownish)",
  collectionDate: 1334938302004,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "2b.Qualitative in-field assessment",
  placemarkId: 732033,
  id: 735245
}, {
  submitterName: "Good- practically always",
  collectionDate: 1334938302005,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "3. Reliability",
  placemarkId: 732033,
  id: 734235
}, {
  submitterName: "Yes",
  collectionDate: 1334938302006,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "B.Sufficient for HHs",
  placemarkId: 732033,
  id: 732228
}, {
  submitterName: "Yes",
  collectionDate: 1334938302006,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "C. Sufficient for livestock",
  placemarkId: 732033,
  id: 735242
}, {
  submitterName: "ahero youth",
  collectionDate: 1334938302007,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "CBO, specify",
  placemarkId: 732033,
  id: 732222
}, {
  submitterName: "Unknown",
  collectionDate: 1334938302008,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Committee in place:",
  placemarkId: 732033,
  id: 735249
}, {
  submitterName: "all",
  collectionDate: 1334938302009,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Community, specify",
  placemarkId: 732033,
  id: 732224
}, {
  submitterName: "Name one",
  collectionDate: 1334938302010,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Contact one",
  placemarkId: 732033,
  id: 735244
}, {
  submitterName: "40",
  collectionDate: 1334938302011,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "D. HHs # served/day",
  placemarkId: 732033,
  id: 735241
}, {
  submitterName: "20/04/12",
  collectionDate: 1334938302012,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Date Record",
  placemarkId: 732033,
  id: 728181
}, {
  submitterName: "No",
  collectionDate: 1334938302013,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Do you have an SPA?",
  placemarkId: 732033,
  id: 734236
}, {
  submitterName: "community",
  collectionDate: 1334938302014,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Funded by",
  placemarkId: 732033,
  id: 728179
}, {
  submitterName: "-0.16252854|35.07743752|1136.800048828125|7gs8a46",
  collectionDate: 1334938302015,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "GPS reading",
  placemarkId: 732033,
  id: 732229
}, {
  submitterName: "alex",
  collectionDate: 1334938302016,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Interviewee",
  placemarkId: 732033,
  id: 728178
}, {
  submitterName: "amara",
  collectionDate: 1334938302017,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Interviewer",
  placemarkId: 732033,
  id: 728180
}, {
  submitterName: "ahero pan",
  collectionDate: 1334938302018,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Name of source/water point",
  placemarkId: 732033,
  id: 728182
}, {
  submitterName: "onuonga",
  collectionDate: 1334938302019,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Names one,specify",
  placemarkId: 732033,
  id: 735247
}, {
  submitterName: "No",
  collectionDate: 1334938302020,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "O&M cost recovery",
  placemarkId: 732033,
  id: 732225
}, {
  submitterName: "LVNWSB",
  collectionDate: 1334938302021,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Organisation",
  placemarkId: 732033,
  id: 728183
}, {
  submitterName: "No",
  collectionDate: 1334938302022,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Part of the piped scheme",
  placemarkId: 732033,
  id: 734229
}, {
  submitterName: "/mnt/sdcard/fieldsurvey/surveyal/8/7/9/8/5/wfpPhoto18652367987985.jpg",
  collectionDate: 1334938302023,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Photo",
  placemarkId: 732033,
  id: 732230
}, {
  submitterName: "No",
  collectionDate: 1334938302024,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Specify none",
  placemarkId: 732033,
  id: 732223
}, {
  submitterName: "tura",
  collectionDate: 1334938302025,
  surveyCode: "Mars / Initial question du planteur",
  deviceIdentifier: "Sub-location",
  placemarkId: 732033,
  id: 734231
}];


FLOW.SurveyQuestionSummary.FIXTURES = [{
    id: 1,
    keyId: 1,
    response:'Apples',
    count:20
}, {
    id: 2,
    keyId: 2,
    response:'Pears',
    count:30
}, {
    id: 3,
    keyId: 3,
    response:'Oranges',
    count:'15'
}, {
    id: 4,
    keyId: 4,
    response:'Mangos',
    count:'45'
}, {
    id: 5,
    keyId: 5,
    response:'Mandarins',
    count:'80'
},{
    id: 5,
    keyId: 5,
    response:'Grapes',
    count:'100'
}];

FLOW.Message.FIXTURES = [{
  id: 1,
  keyId: 1,
  lastUpdateDateTime: 1352149195000,
  userName: "m.t.westra@akvo.org",
  shortMessage: "Published. Please check: http://flowdemo.s3.amazonaws.com/surveys/1417001.xml",
  objectTitle: "Mars surveys/Philly Demo",
  actionAbout: "surveyChangeComplete"
}, {
  id: 2,
  keyId: 2,
  lastUpdateDateTime: 1352149195000,
  userName: "m.t.westra@akvo.org",
  shortMessage: "Published. Please check: http://flowdemo.s3.amazonaws.com/surveys/1417001.xml",
  objectTitle: "Mars surveys/Philly Demo",
  actionAbout: "surveyChangeComplete"
}, {
  id: 3,
  keyId: 3,
  lastUpdateDateTime: 1352149195000,
  userName: "m.t.westra@akvo.org",
  shortMessage: "Published. Please check: http://flowdemo.s3.amazonaws.com/surveys/1417001.xml",
  objectTitle: "Mars surveys/Philly Demo",
  actionAbout: "surveyChangeComplete"
}, {
  id: 4,
  keyId: 4,
  lastUpdateDateTime: 1352149195000,
  userName: "m.t.westra@akvo.org",
  shortMessage: "Published. Please check: http://flowdemo.s3.amazonaws.com/surveys/1417001.xml",
  objectTitle: "Mars surveys/Philly Demo",
  actionAbout: "surveyChangeComplete"
}, {
  id: 5,
  keyId: 5,
  lastUpdateDateTime: 1352149195000,
  userName: "m.t.westra@akvo.org",
  shortMessage: "Published. Please check: http://flowdemo.s3.amazonaws.com/surveys/1417001.xml",
  objectTitle: "Mars surveys/Philly Demo",
  actionAbout: "surveyChangeComplete"
}, {
  id: 5,
  keyId: 5,
  lastUpdateDateTime: 1352149195000,
  userName: "m.t.westra@akvo.org",
  shortMessage: "Published. Please check: http://flowdemo.s3.amazonaws.com/surveys/1417001.xml",
  objectTitle: "Mars surveys/Philly Demo",
  actionAbout: "surveyChangeComplete"
}];

});

loader.register('akvo-flow/models/models-public', function(require) {
// ***********************************************//
//                 models and stores
// ***********************************************//
require('akvo-flow/core-common');
require('akvo-flow/models/store_def-common');

FLOW.BaseModel = DS.Model.extend({
  keyId: DS.attr('number'),
  savingStatus: null,

  // this method calls the checkSaving method on the savingMessageControl, which
  // checks if there are any records inflight. If yes, it sets a boolean,
  // so a saving message can be displayed. savingStatus is used to capture the
  // moment that nothing is being saved anymore, but in the previous event it was
  // so we can turn off the saving message.
  anySaving: function () {
    if (this.get('isSaving') || this.get('isDirty') || this.get('savingStatus')) {
      FLOW.savingMessageControl.checkSaving();
    }
    this.set('savingStatus', (this.get('isSaving') || this.get('isDirty')));
  }.observes('isSaving', 'isDirty')

});

FLOW.SurveyGroup = FLOW.BaseModel.extend({
  didDelete: function () {
    FLOW.surveyGroupControl.populate();
  },
  didUpdate: function () {
    FLOW.surveyGroupControl.populate();
  },
  didCreate: function () {
    FLOW.surveyGroupControl.populate();
  },

  description: DS.attr('string', {
    defaultValue: ''
  }),
  name: DS.attr('string', {
    defaultValue: ''
  }),
  createdDateTime: DS.attr('string', {
    defaultValue: ''
  }),
  lastUpdateDateTime: DS.attr('string', {
    defaultValue: ''
  }),
  // the code field is used as name
  code: DS.attr('string', {
    defaultValue: ''
  })
});


// Explicitly avoid to use belongTo and hasMany as
// Ember-Data lacks of partial loading
// https://github.com/emberjs/data/issues/51
FLOW.PlacemarkDetail = FLOW.BaseModel.extend({
  placemarkId: DS.attr('number'),
  collectionDate: DS.attr('number'),
  order: DS.attr('number'),
  questionText: DS.attr('string'),
  metricName: DS.attr('string'),
  stringValue: DS.attr('string'),
  questionType: DS.attr('string')
});

FLOW.Placemark = FLOW.BaseModel.extend({
  latitude: DS.attr('number'),
  longitude: DS.attr('number'),
  count: DS.attr('number'),
  level: DS.attr('number'),
  surveyId: DS.attr('number'),
  detailsId: DS.attr('number'),
  collectionDate: DS.attr('number')
});

});

loader.register('akvo-flow/models/models', function(require) {
// ***********************************************//
//                 models and stores
// ***********************************************//
require('akvo-flow/core-common');
require('akvo-flow/models/store_def-common');

FLOW.BaseModel = DS.Model.extend({
  keyId: DS.attr('number'),
  savingStatus: null,

  // this method calls the checkSaving method on the savingMessageControl, which
  // checks if there are any records inflight. If yes, it sets a boolean,
  // so a saving message can be displayed. savingStatus is used to capture the
  // moment that nothing is being saved anymore, but in the previous event it was
  // so we can turn off the saving message.
  anySaving: function () {
    if (this.get('isSaving') || this.get('isDirty') || this.get('savingStatus')) {
      FLOW.savingMessageControl.checkSaving();
    }
    this.set('savingStatus', (this.get('isSaving') || this.get('isDirty')));
  }.observes('isSaving', 'isDirty')
});

FLOW.CaddisflyTestDefinition = Ember.Object.extend({
    name: null,
    brand: null,
    uuid: null,

    displayName: function() {
        return this.get('name') + " (" + this.get('brand') +")";
    }.property(''),
});

FLOW.CascadeResource = FLOW.BaseModel.extend({
	name: DS.attr('string', {
	   defaultValue: ''
	}),
	version: DS.attr('number', {
		defaultValue: 1
	}),
	numLevels: DS.attr('number', {
		defaultValue: 1
	}),
	status: DS.attr('string', {
		defaultValue: 'NOT_PUBLISHED'
	}),
	levelNames: DS.attr('array', {defaultValue: []
	}),
});

FLOW.CascadeNode = FLOW.BaseModel.extend({
	name: DS.attr('string', {
	   defaultValue: ''
	}),
	code: DS.attr('string', {
		defaultValue: ''
	}),
	parentNodeId: DS.attr('number', {
		defaultValue: ''
	}),
	cascadeResourceId: DS.attr('number', {
		defaultValue: ''
	})
});

FLOW.SurveyGroup = FLOW.BaseModel.extend({
  description: DS.attr('string', {
    defaultValue: ''
  }),
  name: DS.attr('string', {
    defaultValue: ''
  }),
  path: DS.attr('string', {
    defaultValue: null
  }),
  ancestorIds: DS.attr('array', {
    defaultValue: []
  }),
  createdDateTime: DS.attr('string', {
    defaultValue: ''
  }),
  monitoringGroup: DS.attr('boolean', {
    defaultValue: false
  }),
  newLocaleSurveyId: DS.attr('number'),
  lastUpdateDateTime: DS.attr('string', {
    defaultValue: ''
  }),
  // the code field is used as name
  code: DS.attr('string', {
    defaultValue: ''
  }),

  parentId: DS.attr('number', {
    defaultValue: null
  }),

  projectType: DS.attr('string', {
    defaultValue: "PROJECT"
  }),

  privacyLevel: DS.attr('string', {
    defaultValue: "PRIVATE"
  }),

  defaultLanguageCode: DS.attr('string', {
    defaultValue: "en"
  }),

  published: DS.attr('boolean', {
    defaultValue: false
  }),

  requireDataApproval: DS.attr('boolean', {
      defaultValue: false
  }),

  dataApprovalGroupId: DS.attr('number', {
      defaultValue: null
  }),

  surveyList: DS.attr('array', {
    defaultValue: null
  })

});


FLOW.Survey = FLOW.BaseModel.extend({
  didLoad: function () {
    // set the survey group name
    var sg = FLOW.store.find(FLOW.SurveyGroup, this.get('surveyGroupId'));
    if (!Ember.empty(sg)) {
      this.set('surveyGroupName', sg.get('code'));
    }
  },

  defaultLanguageCode: DS.attr('string'),
  status: DS.attr('string'),
  sector: DS.attr('string'),
  code: DS.attr('string'),
  requireApproval: DS.attr('string'),
  version: DS.attr('string'),
  description: DS.attr('string'),
  name: DS.attr('string'),
  path: DS.attr('string'),
  ancestorIds: DS.attr('array'),
  pointType: DS.attr('string'),
  surveyGroupId: DS.attr('number'),
  createdDateTime: DS.attr('number'),
  lastUpdateDateTime: DS.attr('number'),
  instanceCount: DS.attr('number'),

  // This attribute is used for the 'Copy Survey' functionality
  // Most of the times is `null`
  sourceId: DS.attr('number', {
    defaultValue: null
  }),

  // used in the assignment edit page, not saved to backend
  surveyGroupName: null,

  allowEdit: function () {
	  return !this.get('isNew') && this.get('status') !== 'COPYING';
  }.property('status', 'isNew')

});


FLOW.QuestionGroup = FLOW.BaseModel.extend({
  order: DS.attr('number'),
  name: DS.attr('string'),
  path: DS.attr('string'),
  code: DS.attr('string'),
  surveyId: DS.attr('number'),
  status: DS.attr('string'),
  sourceId: DS.attr('number', {
    defaultValue: null
  }),
  repeatable: DS.attr('boolean', {
    defaultValue: false
  })
});


FLOW.Question = FLOW.BaseModel.extend({
  questionOptions: DS.hasMany('FLOW.QuestionOption'),

  allowDecimal: DS.attr('boolean', {
    defaultValue: false
  }),
  allowMultipleFlag: DS.attr('boolean', {
    defaultValue: false
  }),
  allowOtherFlag: DS.attr('boolean', {
    defaultValue: false
  }),
  localeNameFlag: DS.attr('boolean', {
	    defaultValue: false
	  }),
  localeLocationFlag: DS.attr('boolean', {
		defaultValue: false
  }),
  allowSign: DS.attr('boolean', {
    defaultValue: false
  }),
  geoLocked: DS.attr('boolean', {
	    defaultValue: false
	  }),
  requireDoubleEntry: DS.attr('boolean', {
    defaultValue: false
  }),
  collapseable: DS.attr('boolean', {
    defaultValue: false
  }),
  immutable: DS.attr('boolean', {
    defaultValue: false
  }),
  mandatoryFlag: DS.attr('boolean', {
    defaultValue: true
  }),
  dependentFlag: DS.attr('boolean', {
    defaultValue: false
  }),
  dependentQuestionAnswer: DS.attr('string'),
  dependentQuestionId: DS.attr('number'),
  maxVal: DS.attr('number', {
    defaultValue: null
  }),
  minVal: DS.attr('number', {
    defaultValue: null
  }),
  order: DS.attr('number'),
  cascadeResourceId: DS.attr('number'),
  caddisflyResourceUuid:DS.attr('string'),
  path: DS.attr('string'),
  questionGroupId: DS.attr('number'),
  surveyId: DS.attr('number'),
  variableName: DS.attr('string'),
  metricId: DS.attr('number'),
  text: DS.attr('string'),
  tip: DS.attr('string'),
  type: DS.attr('string', {
	defaultValue: "FREE_TEXT"
  }),
  // This attribute is used for the 'Copy Survey' functionality
  // Most of the times is `null`
  sourceId: DS.attr('number', {
	 defaultValue: null
  }),
  allowExternalSources: DS.attr('boolean', {
    defaultValue: false
  }),
  // Geoshape question type options
  allowPoints: DS.attr('boolean', {
    defaultValue: true
  }),
  allowLine: DS.attr('boolean', {
    defaultValue: true
  }),
  allowPolygon: DS.attr('boolean', {
    defaultValue: true
  })
});


FLOW.QuestionOption = FLOW.BaseModel.extend({
  question: DS.belongsTo('FLOW.Question'),
  order: DS.attr('number'),
  questionId: DS.attr('number'),
  text: DS.attr('string'),
  code: DS.attr('string'),
});


FLOW.DeviceGroup = FLOW.BaseModel.extend({
  code: DS.attr('string', {
    defaultValue: ''
  })
});

FLOW.Device = FLOW.BaseModel.extend({
  didLoad: function () {
    var combinedName;
    if (Ember.empty(this.get('deviceIdentifier'))) {
      combinedName = "no identifer";
    } else {
      combinedName = this.get('deviceIdentifier');
    }
    this.set('combinedName', combinedName + " " + this.get('phoneNumber'));
  },
  esn: DS.attr('string', {
    defaultValue: ''
  }),
  phoneNumber: DS.attr('string', {
    defaultValue: ''
  }),
  deviceIdentifier: DS.attr('string', {
    defaultValue: ''
  }),
  gallatinSoftwareManifest: DS.attr('string'),
  lastKnownLat: DS.attr('number', {
    defaultValue: 0
  }),
  lastKnownLon: DS.attr('number', {
    defaultValue: 0
  }),
  lastKnownAccuracy: DS.attr('number', {
    defaultValue: 0
  }),
  lastPositionDate: DS.attr('number', {
    defaultValue: ''
  }),
  deviceGroup: DS.attr('string', {
    defaultValue: ''
  }),
  deviceGroupName: DS.attr('string', {
    defaultValue: ''
  }),
  isSelected: false,
  combinedName: null
});

FLOW.SurveyAssignment = FLOW.BaseModel.extend({
  name: DS.attr('string'),
  startDate: DS.attr('number'),
  endDate: DS.attr('number'),
  devices: DS.attr('array'),
  surveys: DS.attr('array'),
  language: DS.attr('string')
});

FLOW.SurveyedLocale = DS.Model.extend({
  description: DS.attr('string', {
    defaultValue: ''
  }),
  keyId: DS.attr('number'),
  latitude: DS.attr('number'),
  longitude: DS.attr('number'),
  displayName: DS.attr('string'),
  lastUpdateDateTime: DS.attr('number'),
  surveyGroupId: DS.attr('number'),
  identifier: DS.attr('string'),
  primaryKey: 'keyId'
});

// Explicitly avoid to use belongTo and hasMany as
// Ember-Data lacks of partial loading
// https://github.com/emberjs/data/issues/51
FLOW.PlacemarkDetail = FLOW.BaseModel.extend({
  placemarkId: DS.attr('number'),
  collectionDate: DS.attr('number'),
  order: DS.attr('number'),
  questionText: DS.attr('string'),
  metricName: DS.attr('string'),
  stringValue: DS.attr('string'),
  questionType: DS.attr('string')
});

FLOW.Placemark = FLOW.BaseModel.extend({
	latitude: DS.attr('number'),
	longitude: DS.attr('number'),
	count: DS.attr('number'),
	level: DS.attr('number'),
	surveyId: DS.attr('number'),
	detailsId: DS.attr('number'),
	collectionDate: DS.attr('number')
});

FLOW.SurveyInstance = FLOW.BaseModel.extend({
  approvedFlag: DS.attr('string'),
  approximateLocationFlag: DS.attr('string'),
  surveyInstanceId: DS.attr('number'),
  surveyId: DS.attr('number'),
  surveyedLocaleId:DS.attr('number'),
  collectionDate: DS.attr('number'),
  surveyCode: DS.attr('string'),
  submitterName: DS.attr('string'),
  deviceIdentifier: DS.attr('string'),
  surveyedLocaleIdentifier: DS.attr('string'),
  surveyedLocaleDisplayName: DS.attr('string')
});

FLOW.QuestionAnswer = FLOW.BaseModel.extend({
  value: DS.attr('string'),
  type: DS.attr('string'),
  oldValue: DS.attr('string'),
  surveyId: DS.attr('number'),
  collectionDate: DS.attr('number'),
  surveyInstanceId: DS.attr('number'),
  iteration: DS.attr('number'),
  questionID: DS.attr('string'), //TODO should be number?
  questionText: DS.attr('string')
});

FLOW.ApprovalGroup = FLOW.BaseModel.extend({
    name: DS.attr('string'),
    ordered: DS.attr('boolean'),
});

FLOW.ApprovalStep = FLOW.BaseModel.extend({
    approvalGroupId: DS.attr('number'),
    order: DS.attr('number'),
    title: DS.attr('string'),
    approverUserList: DS.attr('array', {
        defaultValue: null
    }),
});

FLOW.DataPointApproval = FLOW.BaseModel.extend({
    surveyedLocaleId: DS.attr('number'),

    approvalStepId: DS.attr('number'),

    approverUserName: DS.attr('string'),

    approvalDate: DS.attr('number'),

    status: DS.attr('string'),

    comment: DS.attr('string'),
});

FLOW.SurveyQuestionSummary = FLOW.BaseModel.extend({
  response: DS.attr('string'),
  count: DS.attr('number'),
  percentage: null,
  questionId: DS.attr('string')
});

FLOW.User = FLOW.BaseModel.extend({
  userName: DS.attr('string'),
  emailAddress: DS.attr('string'),
  admin: DS.attr('boolean', {
    defaultValue: 0
  }),
  superAdmin: DS.attr('boolean', {
    defaultValue: 0
  }),
  permissionList: DS.attr('string'),
  accessKey: DS.attr('string')
});

FLOW.UserConfig = FLOW.BaseModel.extend({
  group: DS.attr('string'),
  name: DS.attr('string'),
  value: DS.attr('string'),
  userId: DS.attr('number')
});

FLOW.Message = FLOW.BaseModel.extend({
  objectId: DS.attr('number'),
  lastUpdateDateTime: DS.attr('number'),
  userName: DS.attr('string'),
  objectTitle: DS.attr('string'),
  actionAbout: DS.attr('string'),
  shortMessage: DS.attr('string')
});

FLOW.Action = FLOW.BaseModel.extend({});

FLOW.Translation = FLOW.BaseModel.extend({
  didUpdate: function () {
    FLOW.translationControl.putSingleTranslationInList(this.get('parentType'), this.get('parentId'), this.get('text'), this.get('keyId'), false);
  },

  // can't use this at the moment, as the didCreate is fired before the id is back from the ajax call
  // didCreate: function(){
  //   console.log('didCreate',this.get('keyId'));
  //   FLOW.translationControl.putSingleTranslationInList(this.get('parentType'),this.get('parentId'),this.get('text'),this.get('keyId'), false);
  // },

  // temporary hack to fire the didCreate event after the keyId is known
  didCreateId: function () {
    if (!Ember.none(this.get('keyId')) && this.get('keyId') > 0) {
      FLOW.translationControl.putSingleTranslationInList(this.get('parentType'), this.get('parentId'), this.get('text'), this.get('keyId'), false);
    }
  }.observes('this.keyId'),

  didDelete: function () {
    FLOW.translationControl.putSingleTranslationInList(this.get('parentType'), this.get('parentId'), null, null, true);
  },

  parentType: DS.attr('string'),
  parentId: DS.attr('string'),
  surveyId: DS.attr('string'),
  questionGroupId: DS.attr('string'),
  text: DS.attr('string'),
  langCode: DS.attr('string')
});


FLOW.NotificationSubscription = FLOW.BaseModel.extend({
  notificationDestination: DS.attr('string'),
  notificationOption: DS.attr('string'),
  notificationMethod: DS.attr('string'),
  notificationType: DS.attr('string'),
  expiryDate: DS.attr('number'),
  entityId: DS.attr('number')
});

FLOW.SubCountry = FLOW.BaseModel.extend({
  countryCode: DS.attr('string'),
  level: DS.attr('number'),
  name: DS.attr('string'),
  parentKey: DS.attr('number'),
  parentName: DS.attr('string')
});

});

loader.register('akvo-flow/models/store_def-common', function(require) {
FLOW.store = DS.Store.create({
  revision: 10,
  adapter: DS.FLOWRESTAdapter.create({
    bulkCommit: false,
    namespace: "rest",
    url: window.location.protocol + "//" + window.location.hostname +
         (window.location.port ? ':' + window.location.port : '')
  })
});

DS.JSONTransforms.array = {
  deserialize: function (serialized) {
    return Ember.none(serialized) ? null : serialized;
  },

  serialize: function (deserialized) {
    return Ember.none(deserialized) ? null : deserialized;
  }
};

});

loader.register('akvo-flow/router/router-public', function(require) {
require('akvo-flow/core-common');

FLOW.Router = Ember.Router.extend({
  enableLogging: true,
  loggedIn: false,
  location: 'none',
  root: Ember.Route.extend({
    doNavMaps: function (router, context) {
      router.transitionTo('navMaps');
    },
    index: Ember.Route.extend({
      route: '/',
      redirectsTo: 'navMaps'
    }),

    // ************************** MAPS ROUTER **********************************
    navMaps: Ember.Route.extend({
      route: '/maps',
      connectOutlets: function (router, context) {
        router.get('applicationController').connectOutlet('navMaps');
      }
    })
  })
});

});

loader.register('akvo-flow/router/router', function(require) {
// ***********************************************//
//                 Router
// ***********************************************//
require('akvo-flow/core-common');

FLOW.Router = Ember.Router.extend({
  enableLogging: true,
  loggedIn: false,
  location: 'none',
  //'hash'or 'none' for URLs

  resetState: function () {
    // We could have unsaved changes
    FLOW.store.commit();

    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedSurveyGroup', null);
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.selectedControl.set('selectedQuestion', null);
    FLOW.selectedControl.set('selectedCascadeResource', null);
    FLOW.selectedControl.set('cascadeImportNumLevels', null);
    FLOW.selectedControl.set('cascadeImportIncludeCodes', null);
    FLOW.surveyControl.set('content', null);
    FLOW.questionControl.set('OPTIONcontent', null);
    FLOW.metaControl.set('since', null);
  },

  root: Ember.Route.extend({
    doNavSurveys: function (router, context) {
      router.transitionTo('navSurveys.index');
    },
    doNavDevices: function (router, context) {
      router.transitionTo('navDevices.index');
    },
    doNavData: function (router, context) {
      router.transitionTo('navData.index');
    },
    doNavReports: function (router, context) {
      router.transitionTo('navReports.index');
    },
    doNavMaps: function (router, context) {
      router.transitionTo('navMaps');
    },
    doNavUsers: function (router, context) {
      router.transitionTo('navUsers');
    },
    doNavMessages: function (router, context) {
      router.transitionTo('navMessages');
    },
    // not used at the moment
    doNavAdmin: function (router, context) {
      router.transitionTo('navAdmin');
    },

    // non-working code for transitioning to navHome at first entry of the app
    //    setup: function(router){
    //      router.send("goHome");
    //    },
    //    goHome:function(router){
    //      router.transitionTo('navHome');
    //    },
    index: Ember.Route.extend({
      route: '/',
      redirectsTo: 'navSurveys.index'
    }),

    // ******************* SURVEYS ROUTER ********************
    navSurveys: Ember.Route.extend({
      route: '/surveys',
      connectOutlets: function (router, event) {
        router.get('applicationController').connectOutlet('navSurveys');
        router.set('navigationController.selected', 'navSurveys');
      },

      doNewSurvey: function (router, event) {
        router.transitionTo('navSurveys.navSurveysNew');
      },

      doEditSurvey: function (router, event) {
        FLOW.selectedControl.set('selectedSurvey', event.context);
        router.transitionTo('navSurveys.navSurveysEdit.index');
      },

      doSurveysMain: function (router, event) {
        FLOW.selectedControl.set('selectedQuestion', null);
        router.transitionTo('navSurveys.navSurveysMain');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'navSurveysMain'
      }),

      navSurveysMain: Ember.Route.extend({
        route: '/main',
        connectOutlets: function (router, event) {
          router.get('navSurveysController').connectOutlet({
            name: 'navSurveysMain'
          });
          FLOW.projectControl.populate();
          FLOW.cascadeResourceControl.populate();
          FLOW.projectControl.set('currentProject', null);
          FLOW.projectControl.set('newlyCreated', null);
          FLOW.selectedControl.set('selectedQuestionGroup', null);
          FLOW.selectedControl.set('selectedSurvey', null);
          FLOW.selectedControl.set('selectedQuestion', null);
          FLOW.questionControl.set('OPTIONcontent', null);
        }
      }),

      navSurveysNew: Ember.Route.extend({
        route: '/new',
        connectOutlets: function (router, event) {
          var newSurvey;

          newSurvey = FLOW.store.createRecord(FLOW.Survey, {
            "name": "",
            "defaultLanguageCode": "en",
            "requireApproval": false,
            "status": "NOT_PUBLISHED",
            "surveyGroupId": FLOW.selectedControl.selectedSurveyGroup.get('keyId'),
            "version":"1.0"
          });

          FLOW.selectedControl.set('selectedSurvey', newSurvey);
          router.transitionTo('navSurveys.navSurveysEdit.index');
        }
      }),

      navSurveysEdit: Ember.Route.extend({
        route: '/edit',
        connectOutlets: function (router, event) {
          router.get('navSurveysController').connectOutlet({
            name: 'navSurveysEdit'
          });
          // all questions should be closed when we enter
          FLOW.selectedControl.set('selectedQuestion', null);
        },

        doEditQuestions: function (router, event) {
          router.transitionTo('navSurveys.navSurveysEdit.editQuestions');
        },

        index: Ember.Route.extend({
          route: '/',
          redirectsTo: 'editQuestions'
        }),

        manageNotifications: Ember.Route.extend({
          route: '/notifications',
          connectOutlets: function (router, event) {
            router.get('navSurveysEditController').connectOutlet({
              name: 'manageNotifications'
            });
            FLOW.notificationControl.populate();
          }
        }),

        manageTranslations: Ember.Route.extend({
          route: '/translations',
          connectOutlets: function (router, event) {
            router.get('navSurveysEditController').connectOutlet({
              name: 'manageTranslations'
            });
            FLOW.translationControl.populate();
          }
        }),

        editQuestions: Ember.Route.extend({
          route: '/questions',
          connectOutlets: function (router, event) {
            router.get('navSurveysEditController').connectOutlet({
              name: 'editQuestions'
            });

          }
        })
      })
    }),

    //********************** DEVICES ROUTER *******************
    navDevices: Ember.Route.extend({
      route: '/devices',
      connectOutlets: function (router, event) {
        router.get('applicationController').connectOutlet('navDevices');
        router.set('navigationController.selected', 'navDevices');
      },

      doCurrentDevices: function (router, event) {
        router.transitionTo('navDevices.currentDevices');
      },

      doAssignSurveysOverview: function (router, event) {
        router.transitionTo('navDevices.assignSurveysOverview');
      },

      doEditSurveysAssignment: function (router, event) {
        router.transitionTo('navDevices.editSurveysAssignment');
      },

      doSurveyBootstrap: function (router, event) {
        router.transitionTo('navDevices.surveyBootstrap');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'currentDevices'
      }),

      currentDevices: Ember.Route.extend({
        route: '/current-devices',
        connectOutlets: function (router, context) {
          router.get('navDevicesController').connectOutlet('currentDevices');
          router.resetState();
          FLOW.deviceGroupControl.populate();
          FLOW.deviceControl.populate();
          router.set('devicesSubnavController.selected', 'currentDevices');
        }
      }),

      assignSurveysOverview: Ember.Route.extend({
        route: '/assign-surveys',
        connectOutlets: function (router, context) {
          router.get('navDevicesController').connectOutlet('assignSurveysOverview');
          FLOW.surveyAssignmentControl.populate();
          router.set('devicesSubnavController.selected', 'assignSurveys');
        }
      }),

      editSurveysAssignment: Ember.Route.extend({
        route: '/assign-surveys',
        connectOutlets: function (router, context) {
          router.get('navDevicesController').connectOutlet('editSurveyAssignment');
          router.set('devicesSubnavController.selected', 'assignSurveys');
        }
      }),

      surveyBootstrap: Ember.Route.extend({
        route: '/manual-transfer',
        connectOutlets: function (router, context) {
          router.get('navDevicesController').connectOutlet('surveyBootstrap');
          router.set('devicesSubnavController.selected', 'surveyBootstrap');
        }
      })
    }),


    // ******************* DATA ROUTER ***********************
    navData: Ember.Route.extend({
      route: '/data',
      connectOutlets: function (router, event) {
        router.get('applicationController').connectOutlet('navData');
        router.set('navigationController.selected', 'navData');
      },

      doInspectData: function (router, event) {
        router.transitionTo('navData.inspectData');
      },
      doBulkUpload: function (router, event) {
        router.transitionTo('navData.bulkUpload');
      },
      doDataCleaning: function (router, event) {
        router.transitionTo('navData.dataCleaning');
      },
      doCascadeResources: function (router, event) {
          router.transitionTo('navData.cascadeResources');
        },
      doMonitoringData: function (router, event) {
        router.transitionTo('navData.monitoringData');
      },

      doDataApproval: function (router, event) {
          router.transitionTo('navData.dataApproval.listApprovalGroups');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'inspectData'
      }),

      inspectData: Ember.Route.extend({
        route: '/inspectdata',
        connectOutlets: function (router, context) {
          router.get('navDataController').connectOutlet('inspectData');
          router.set('datasubnavController.selected', 'inspectData');
          router.resetState();
        }
      }),

      bulkUpload: Ember.Route.extend({
        route: '/bulkupload',
        connectOutlets: function (router, context) {
          router.get('navDataController').connectOutlet('bulkUpload');
          router.set('datasubnavController.selected', 'bulkUpload');
        }
      }),

      dataCleaning: Ember.Route.extend({
        route: '/datacleaning',
        connectOutlets: function (router, context) {
          router.get('navDataController').connectOutlet('dataCleaning');
          router.set('datasubnavController.selected', 'dataCleaning');
          router.resetState();
        }
      }),

      cascadeResources: Ember.Route.extend({
          route: '/cascaderesources',
          connectOutlets: function (router, context) {
            router.get('navDataController').connectOutlet('cascadeResources');
            router.set('datasubnavController.selected', 'cascadeResources');
            FLOW.cascadeResourceControl.populate();
          }
        }),

      monitoringData: Ember.Route.extend({
        route: '/monitoringdata',
        connectOutlets: function (router, context) {
          router.get('navDataController').connectOutlet('monitoringData');
          router.set('datasubnavController.selected', 'monitoringData');
          router.resetState();
        }
      }),

      dataApproval: Ember.Route.extend({
          route: '/dataapproval',

          connectOutlets: function (router, context) {
              router.get('navDataController').connectOutlet('dataApproval');
              router.set('datasubnavController.selected', 'approvalGroup');
          },

          doAddApprovalGroup: function (router, event) {
              router.get('approvalGroupController').add();
              router.get('approvalStepsController').loadByGroupId();
              router.transitionTo('navData.dataApproval.editApprovalGroup');
          },

          doEditApprovalGroup: function (router, event) {
              var groupId = event.context.get('keyId');
              var lastLoadedGroup = router.get('approvalGroupController').get('content');
              if (!lastLoadedGroup || (lastLoadedGroup.get('keyId') !== groupId)) {
                  router.get('approvalGroupController').load(groupId);
                  router.get('approvalStepsController').loadByGroupId(groupId);
              }
              router.transitionTo('navData.dataApproval.editApprovalGroup');
          },

          doSaveApprovalGroup: function (router, event) {
              router.get('approvalGroupController').save();
              router.transitionTo('navData.dataApproval.listApprovalGroups');
          },

          doCancelEditApprovalGroup: function (router, event) {
              router.get('approvalGroupController').cancel();
              router.transitionTo('navData.dataApproval.listApprovalGroups');
          },

          doDeleteApprovalGroup: function (router, event) {
              var group = event.context;
              router.get('approvalGroupListController').delete(group);
          },

          // default route for dataApproval tab
          listApprovalGroups: Ember.Route.extend({
              route: '/list',

              connectOutlets: function (router, context) {
                  router.get('dataApprovalController').connectOutlet('approvalMain', 'approvalGroupList');
                  var approvalList = router.get('approvalGroupListController');
                  if (!approvalList.get('content')) {
                      approvalList.set('content', FLOW.ApprovalGroup.find())
                  }
              },
          }),

          editApprovalGroup: Ember.Route.extend({
              route: '/approvalsteps',

              connectOutlets: function (router, event) {
                  router.get('dataApprovalController').connectOutlet('approvalMain', 'approvalGroup');
                  router.get('approvalGroupController').connectOutlet('approvalStepsOutlet', 'approvalSteps');
              },
          }),
      }),
    }),

    // ************************** REPORTS ROUTER **********************************
    navReports: Ember.Route.extend({
      route: '/reports',
      connectOutlets: function (router, context) {
        router.get('applicationController').connectOutlet('navReports');
        router.resetState();
        router.set('navigationController.selected', 'navReports');
      },

      doExportReports: function (router, event) {
        router.transitionTo('navReports.exportReports');
      },

      doChartReports: function (router, event) {
        router.transitionTo('navReports.chartReports');
      },

      index: Ember.Route.extend({
        route: '/',
        redirectsTo: 'exportReports'
      }),

      exportReports: Ember.Route.extend({
        route: '/exportreports',
        connectOutlets: function (router, context) {
          router.get('navReportsController').connectOutlet('exportReports');
          router.set('reportsSubnavController.selected', 'exportReports');
          router.resetState();
        }
      }),

      chartReports: Ember.Route.extend({
        route: '/chartreports',
        connectOutlets: function (router, context) {
          router.resetState();
          router.get('navReportsController').connectOutlet('chartReports');
          router.set('reportsSubnavController.selected', 'chartReports');
        }
      })
    }),

    // ************************** MAPS ROUTER **********************************
    navMaps: Ember.Route.extend({
      route: '/maps',
      connectOutlets: function (router, context) {
        router.get('applicationController').connectOutlet('navMaps');
        router.set('navigationController.selected', 'navMaps');
      }
    }),

    // ************************** USERS ROUTER **********************************
    navUsers: Ember.Route.extend({
      route: '/users',
      connectOutlets: function (router, context) {
        router.get('applicationController').connectOutlet('navUsers');
        router.set('navigationController.selected', 'navUsers');
      }
    }),

    // ************************** MESSAGES ROUTER **********************************
    navMessages: Ember.Route.extend({
      route: '/users',
      connectOutlets: function (router, context) {
        router.get('applicationController').connectOutlet('navMessages');
        router.set('navigationController.selected', 'navMessages');
        FLOW.messageControl.populate();
        router.resetState();
      }
    }),

    // ************************** ADMIN ROUTER **********************************
    // not used at the moment
    navAdmin: Ember.Route.extend({
      route: '/admin',
      connectOutlets: function (router, context) {
        router.get('applicationController').connectOutlet('navAdmin');
        router.set('navigationController.selected', 'navAdmin');
      }
    })
  })
});

});

loader.register('akvo-flow/templ-common', function(require) {
var get = Ember.get,
  fmt = Ember.String.fmt;

Ember.View.reopen({
  templateForName: function (name, type) {
    if (!name) {
      return;
    }

    var templates = get(this, 'templates'),
      template = get(templates, name);

    if (!template) {
      try {
        template = require('akvo-flow/templates/' + name);
      } catch (e) {
        throw new Ember.Error(fmt('%@ - Unable to find %@ "%@".', [this, type, name]));
      }
    }

    return template;
  }
});

});

loader.register('akvo-flow/views/data/bulk-upload-view', function(require) {
/*global Resumable, FLOW, $, Ember */

FLOW.uuid = function (file) {
  return Math.uuidFast();
};

FLOW.uploader = Ember.Object.create({
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
      var li = $('#resumable-file-'+file.uniqueIdentifier);
      FLOW.uploader.set('cancelled', false);

      // Show progress pabr
      $('.resumable-list').show();
      if (li.length === 0) {
        $(".resumable-list").append("<li id='resumable-file-"+ file.uniqueIdentifier + "'></li>").scrollTop($('.resumable-list').outerHeight(true));
      }
      // Add the file to the list
      if (file.file.type !== "application/zip" && file.file.type !== "application/x-zip-compressed") {
        $(".resumable-progress").hide();
        $("#resumable-file-"+ file.uniqueIdentifier).html(
          "<span class='resumable-file-name'>"+file.fileName+"</span>"
                +  Ember.String.loc('_unsupported_file_type')
                + "<img src='images/infolnc.png' class='unsupportedFile uploadStatus'> ");
        $("#resumable-file-"+ file.uniqueIdentifier).css({
            color: '#FF0000'});
        r.removeFile(file); //remove file
      } else {
        $('.resumable-progress').show();
        $("#resumable-file-"+ file.uniqueIdentifier).html(
          '<span class="resumable-file-name">'+file.fileName+'</span>'
          +'<span id="resumable-file-progress-'+file.uniqueIdentifier+'" class="uploadStatus"></span>'
          +'<div id="progress-bar-'+file.uniqueIdentifier+'" class="progress-bar"></div>').css('position','sticky');

        $('#progress-bar-'+file.uniqueIdentifier).css({
          width: '0%'
        });
        // Actually start the upload
        r.upload();
      }
    });

    r.on('pause', function () {
      // Show resume, hide pause
      $('.resumable-progress .progress-resume-link').show();
      $('.resumable-progress .progress-pause-link').hide();
    });

    r.on('complete', function () {
      // Hide pause/resume when the upload has completed
      $('.resumable-progress .progress-resume-link, .resumable-progress .progress-pause-link').hide();
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
      $("#resumable-file-"+ file.uniqueIdentifier).html(
        '<span class="resumable-file-name">'+file.fileName+'</span>'
        +'<img src = "images/tickBox.svg" class = "uploadComplete uploadStatus">'
      ).slideDown( "slow", function() {});
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
      $('#resumable-file-progress-'+file.uniqueIdentifier).html('('+ Ember.String.loc('_file_could_not_upload')+': ' + message + ')');
    });

    r.on('fileProgress', function (file) {
      // Handle progress for both the file and the overall upload
      $('#resumable-file-progress-'+file.uniqueIdentifier).html(Ember.String.loc('_uploading')+Math.floor(file.progress() * 100) + '%');
      $('#progress-bar-'+file.uniqueIdentifier).css({
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
  }
});

FLOW.BulkUploadAppletView = FLOW.View.extend({
  didInsertElement: function () {
    FLOW.uploader.assignDrop($('.resumable-drop')[0]);
    FLOW.uploader.assignBrowse($('.resumable-browse')[0]);
    FLOW.uploader.registerEvents();
  },
  willDestroyElement: function () {
    FLOW.uploader.set('cancelled', FLOW.uploader.isUploading());
    FLOW.uploader.cancel();
    this._super();
  }
});

/* Show warning when trying to close the tab/window with an upload process in progress */
window.onbeforeunload = function (e) {
  var confirmationMessage = Ember.String.loc('_upload_in_progress');

  if (FLOW.uploader.isUploading()) {
    (e || window.event).returnValue = confirmationMessage;
    return confirmationMessage;
  }
};

});

loader.register('akvo-flow/views/data/cascade-resources-view', function(require) {
function capitaliseFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

if (!String.prototype.trim) {
		String.prototype.trim=function(){return this.replace(/^\s+|\s+$/g, '');};
}



FLOW.CascadeResourceView = FLOW.View.extend({
	showNewCascadeField: false,
	cascadeResourceName:null,
	showImportDialog:false,

    didInsertElement : function() {
      FLOW.uploader.registerEvents();
    },

    showMessage : function(header, msg) {
		FLOW.dialogControl.set('activeAction', 'ignore');
		FLOW.dialogControl.set('header', header);
		FLOW.dialogControl.set('message', msg);
		FLOW.dialogControl.set('showCANCEL', false);
		FLOW.dialogControl.set('showDialog', true);
	},

    importFile : function() {
		var file = $('#cascade-resource-file')[0],
		    numLevels = FLOW.selectedControl.get('cascadeImportNumLevels');

		if (!numLevels || +numLevels === 0) {
			this.showMessage(Ember.String.loc('_import_cascade_file'), Ember.String.loc('_import_cascade_number_levels'));
			return;
		}

		if (!file || file.files.length === 0) {
			this.showMessage(Ember.String.loc('_import_cascade_file'), Ember.String.loc('_import_select_cascade_file'));
			return;
		}
		FLOW.uploader.addFile(file.files[0]);
		FLOW.uploader.upload();
	},

	// fired when 'add a cascade resource' is clicked. Displays a text field
	newCascade: function () {
		FLOW.selectedControl.set('cascadeImportNumLevels', null);
		FLOW.selectedControl.set('cascadeImportIncludeCodes', null);
		this.set('importIncludeCodes', null);
	    this.set('showNewCascadeField', true);
	},

	saveNodes: function(){
		FLOW.store.commit();
	},

	showImportCascade: function(){
		this.toggleProperty('showImportDialog');
	},

	hideImportCascade: function(){
		FLOW.selectedControl.set('cascadeImportNumLevels', null);
		FLOW.selectedControl.set('cascadeImportIncludeCodes', null);
		this.toggleProperty('showImportDialog');
	},

	publishResource: function(){
		if (!Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'))){
			FLOW.cascadeResourceControl.publish(FLOW.selectedControl.selectedCascadeResource.get('keyId'));
			this.showMessage(Ember.String.loc('_cascade_resources'), Ember.String.loc('_cascade_resource_published_text'));
		}
	},

	deleteResource: function (e) {
		var resource = FLOW.selectedControl.selectedCascadeResource,
		    keyId = resource.get('keyId'),
            questions = FLOW.store.filter(FLOW.Question, function (item) {
              return item.get('cascadeResourceId') === keyId;
            });

		if (questions.get('length') > 0) {
			this.showMessage(Ember.String.loc('_cascade_resources'), Ember.String.loc('_cannot_delete_cascade'));
			return;
		}
		resource.deleteRecord();
		FLOW.store.commit();
		FLOW.selectedControl.set('selectedCascadeResource', null);
	},

	// adds a level to the hierarchy
	addLevel: function(){
		var selectedCascade = FLOW.selectedControl.selectedCascadeResource,
		    numLevels = (selectedCascade && selectedCascade.get('numLevels') + 1) || 0,
		    nameLevels = (selectedCascade && selectedCascade.get('levelNames')) || [];

		if (!selectedCascade) {
			return;
		}

		nameLevels.push('Level ' + numLevels);

		selectedCascade.set('numLevels', numLevels);
		selectedCascade.set('levelNames', nameLevels);
		selectedCascade.set('status', 'NOT_PUBLISHED');
		FLOW.cascadeResourceControl.triggerStatusUpdate();
		FLOW.store.commit();
		FLOW.cascadeResourceControl.setLevelNamesArray();
		FLOW.cascadeResourceControl.setDisplayLevelNames();
	},

	oneSelected: function(){
		return !Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'));
	}.property('FLOW.selectedControl.selectedCascadeResource').cacheable(),

	resourceSelected: function(){
		if (!Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'))){
			var i=1, levelNamesArray=[];
			FLOW.cascadeNodeControl.emptyNodes(1);
			FLOW.cascadeNodeControl.populate(FLOW.selectedControl.selectedCascadeResource.get('keyId'),1,0);
			FLOW.cascadeResourceControl.setLevelNamesArray();
			FLOW.cascadeNodeControl.set('skip',0);
			FLOW.cascadeNodeControl.setDisplayLevels();
			FLOW.cascadeResourceControl.setDisplayLevelNames();
			FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
		}
	}.observes('FLOW.selectedControl.selectedCascadeResource'),

	// fired when 'save' is clicked while showing new cascade text field. Saves new cascade resource to the data store
	saveNewCascadeResource: function () {
		var casc;
		  if (!Ember.empty(this.get('cascadeResourceName').trim())){
			  casc = FLOW.store.createRecord(FLOW.CascadeResource, {
				  "version": 0,
				  "levelNames":["Level 1"],
				  "numLevels": 1,
				  "name": capitaliseFirstLetter(this.get('cascadeResourceName'))
			  });
			  FLOW.store.commit();
			  FLOW.selectedControl.set('selectedCascadeResource',casc);
		  }
	    this.set('showNewCascadeField', false);
	    this.set('cascadeResourceName',null);
	},

	  // fired when 'cancel' is clicked while showing new group text field in left sidebar. Cancels the new survey group creation
	cancelNewCascadeResource: function () {
	  this.set('cascadeResourceName', null);
	  this.set('showNewCascadeField', false);
	},

	hideColumn2: function () {
		var cascade = FLOW.selectedControl.selectedCascadeResource;
		if (!cascade) {
			return false;
		}
		return cascade.get('numLevels') < 2;
	}.property('FLOW.selectedControl.selectedCascadeResource', 'FLOW.selectedControl.selectedCascadeResource.numLevels'),

	hideColumn3: function() {
		var cascade = FLOW.selectedControl.selectedCascadeResource;
		if (!cascade) {
			return false;
		}
		return cascade.get('numLevels') < 3;
	}.property('FLOW.selectedControl.selectedCascadeResource', 'FLOW.selectedControl.selectedCascadeResource.numLevels')
});

FLOW.CascadeSecondNavView = FLOW.View.extend({
	tagName: 'li',
	content: null,
	classNameBindings: 'display:disable'.w(),

	display: function(){
		if (this.get('dir') == "up"){
			return !this.get('showGoUpLevel');
		} else return !this.get('showGoDownLevel');
	}.property('this.showGoUpLevel','this.showGoUpLevel'),

	showGoUpLevel: function(){
		return FLOW.selectedControl.selectedCascadeResource && (FLOW.cascadeNodeControl.get('skip') + 3 < FLOW.selectedControl.selectedCascadeResource.get('numLevels'));
	}.property('FLOW.cascadeNodeControl.skip', 'FLOW.selectedControl.selectedCascadeResource'),

	showGoDownLevel: function(){
		return FLOW.cascadeNodeControl.get('skip') > 0;
	}.property('FLOW.cascadeNodeControl.skip','FLOW.selectedControl.selectedCascadeResource'),

	goUpLevel: function(){
		FLOW.cascadeNodeControl.set('skip', FLOW.cascadeNodeControl.get('skip') + 1);
		FLOW.cascadeNodeControl.setDisplayLevels();
		FLOW.cascadeResourceControl.setDisplayLevelNames();
		FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
	},

	goDownLevel: function(){
		FLOW.cascadeNodeControl.set('skip', FLOW.cascadeNodeControl.get('skip') - 1);
		FLOW.cascadeNodeControl.setDisplayLevels();
		FLOW.cascadeResourceControl.setDisplayLevelNames();
		FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
	},
});

FLOW.CascadeLevelBreadcrumbView = FLOW.View.extend({
	tagName: 'li',
	content: null,
	classNameBindings: 'offscreen:offScreen'.w(),

	offscreen: function() {
		var skip = FLOW.cascadeNodeControl.get('skip');
		var level = this.content.get('level');
		return ((level < skip + 1) || (level > skip + 3));
	}.property('FLOW.cascadeNodeControl.skip', 'FLOW.selectedControl.selectedCascadeResource'),

	adaptColView: function(){
		var skip = FLOW.cascadeNodeControl.get('skip');
		var level = this.content.get('level');
		// if the level is already visible, do nothing
		if (level > skip && level < skip + 4) {
			return;
		}

		// clicked level lies on the left
		if (level < skip + 1) {
			FLOW.cascadeNodeControl.set('skip', level - 1);
			FLOW.cascadeNodeControl.setDisplayLevels();
			FLOW.cascadeResourceControl.setDisplayLevelNames();
			FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
		}

		// clicked level lies on the right
		if (level > skip + 3) {
			FLOW.cascadeNodeControl.set('skip', level - 3);
			FLOW.cascadeNodeControl.setDisplayLevels();
			FLOW.cascadeResourceControl.setDisplayLevelNames();
			FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
		}
	},
});

FLOW.CascadeLevelNameView = FLOW.View.extend({
	editFieldVisible:false,
	content: null,
	levelName:null,

	showEditField: function(){
		this.set('levelName',this.get('origLevelName'));
		this.set('editFieldVisible',true);
	},

	cancelNewLevelName: function(){
		this.set('levelName',null);
		this.set('editFieldVisible',false);
	},

	saveNewLevelName: function(){
		var currList, index, i=1, levelNamesArray=[];
		index = this.get('col') + FLOW.cascadeNodeControl.get('skip');
		currList = FLOW.selectedControl.selectedCascadeResource.get('levelNames');
		currList[index-1] = capitaliseFirstLetter(this.get('levelName'));
		FLOW.selectedControl.selectedCascadeResource.set('levelNames',currList);

		// this is needed, as in this version of Ember, changes in an array do
		// not make an object dirty, apparently
		FLOW.selectedControl.selectedCascadeResource.send('becomeDirty');
		FLOW.selectedControl.selectedCascadeResource.set('status', 'NOT_PUBLISHED');
		FLOW.cascadeResourceControl.triggerStatusUpdate();
		FLOW.store.commit();

		// put the names in the array again.
		FLOW.cascadeResourceControl.setLevelNamesArray();
		FLOW.cascadeResourceControl.setDisplayLevelNames();

		this.set('levelName',null);
		this.set('editFieldVisible',false);
	}
});

FLOW.CascadeNodeView = FLOW.View.extend({
	cascadeNodeName: null,
	cascadeNodeCode:null,

	showInputField:function(){
		var skip = FLOW.cascadeNodeControl.get('skip');

		// determines if we should show an input field in this column
		// we do this in column one by default, or if in the previous column a node has been selected
		if (this.get('col') === 1 && skip === 0) {
			return true;
		}
		return (!Ember.empty(FLOW.cascadeNodeControl.selectedNode[skip + this.get('col') - 1]) &&
				!Ember.empty(FLOW.cascadeNodeControl.selectedNode[skip + this.get('col') - 1].get('keyId')));
	}.property('FLOW.cascadeNodeControl.selectedNodeTrigger').cacheable(),

	addNewNode: function() {
		var newNodeStringArray, level, nodes, exists, item, itemTrim, levelNames;
		level = this.get('col') + FLOW.cascadeNodeControl.get('skip');
		nodes = FLOW.cascadeNodeControl.get('level' + level);
		item = this.get('cascadeNodeName');
		if (item !== null && item.trim().length > 0) {
			exists = false;
			itemTrim = item.trim().toLowerCase();
			nodes.forEach(function(node){
				if (node.get('name').toLowerCase() == itemTrim) {
					exists = true;
				}
			});
			if (!exists) {
				FLOW.cascadeNodeControl.addNode(FLOW.selectedControl.selectedCascadeResource.get('keyId'),
						level, item.trim(), this.get('cascadeNodeCode'));
			}
		}
		this.set('cascadeNodeName',"");
		this.set('cascadeNodeCode',"");

		// check if we need to increase the level of items that we use
		// TODO somehow decrease it when a level becomes empty. However, this is hard to check.
		if (level > FLOW.selectedControl.selectedCascadeResource.get('numLevels')){
			levelNames = FLOW.selectedControl.selectedCascadeResource.get('levelNames');
			levelNames.push('Level ' + level);
			FLOW.selectedControl.selectedCascadeResource.set('numLevels', level);
			FLOW.selectedControl.selectedCascadeResource.set('levelNames', levelNames);
			FLOW.store.commit();
			FLOW.cascadeResourceControl.setLevelNamesArray();
		}
	},
});

FLOW.CascadeNodeItemView = FLOW.View.extend({
	content: null,
	tagName: 'li',
	classNameBindings: 'amSelected:selected'.w(),
	showEditNodeFlag: false,
	newCode:null,
	newName:null,

	// true if the node group is selected. Used to set proper display class
	amSelected: function () {
	    var selected = FLOW.cascadeNodeControl.get('selectedNode')[this.get('col') + FLOW.cascadeNodeControl.get('skip')];
	    if (selected) {
	      var amSelected = (this.content.get('name') === FLOW.cascadeNodeControl.get('selectedNode')[this.get('col') + FLOW.cascadeNodeControl.get('skip')].get('name'));
	      return amSelected;
	    } else {
	      return false;
	    }
	}.property('FLOW.cascadeNodeControl.selectedNodeTrigger').cacheable(),

	deleteNode:function() {
		FLOW.cascadeNodeControl.emptyNodes(this.get('col') + FLOW.cascadeNodeControl.get('skip') + 1);
		this.get('content').deleteRecord();
		FLOW.store.commit();
	},

	showEditNodeField: function(){
		this.set('showEditNode', true);
		this.set('newCode',this.content.get('code'));
		this.set('newName',this.content.get('name'));
	},

	cancelEditNode: function(){
		this.set('showEditNode', false);
		this.set('newCode',null);
		this.set('newName',null);
	},

	saveEditNode: function(){
		if (Ember.empty(this.get('newName')) ||this.get('newName').trim().length === 0) {
			this.cancelEditNode();
			return;
		}
		this.content.set('name',capitaliseFirstLetter(this.get('newName')));
		this.content.set('code',this.get('newCode'));
		FLOW.store.commit();
		this.cancelEditNode();
	},

	makeSelected: function(){
		var i, level;
		level = this.get('col') + FLOW.cascadeNodeControl.get('skip');
		FLOW.cascadeNodeControl.get('selectedNode')[level] = this.get('content');
		FLOW.cascadeNodeControl.emptyNodes(level + 1);

		if (!Ember.empty(this.content.get('keyId'))){
			FLOW.cascadeNodeControl.populate(FLOW.selectedControl.selectedCascadeResource.get('keyId'),
					level + 1, this.content.get('keyId'));
		}
		FLOW.cascadeNodeControl.toggleSelectedNodeTrigger();
	}
});
});

loader.register('akvo-flow/views/data/data-approval-views', function(require) {
FLOW.DataApprovalView = Ember.View.extend({
    /*
     * This is a wrapper template for the data approval tab.
     * It provides a container view for the two views,
     *  1. list approvals
     *  2. edit approval steps
     * that shall be displayed under the Data > Data Approval tab
     * It has been created to avoid mixing the data approval views
     * and controllers with the data tab views and controllers
     */
    template: Ember.Handlebars.compile('{{outlet approvalMain}}'),
});

FLOW.ApprovalGroupListView = Ember.View.extend({
    templateName: 'navData/data-approval-group-list'
});

FLOW.ApprovalGroupView = Ember.View.extend({
    templateName: 'navData/data-approval-group',

    approvalTypeOptions: [{label: Ember.String.loc('_ordered'), optionValue: "ordered"},
                          {label: Ember.String.loc('_unordered'), optionValue: "unordered"}],
});


FLOW.ApprovalStepsView = Ember.View.extend({
    templateName: 'navData/data-approval-steps',
});
});

loader.register('akvo-flow/views/data/inspect-data-table-views', function(require) {
FLOW.inspectDataTableView = FLOW.View.extend({
  selectedSurvey: null,
  surveyInstanceId: null,
  surveyId: null,
  deviceId: null,
  submitterName: null,
  beginDate: null,
  endDate: null,
  since: null,
  alreadyLoaded: [],
  selectedCountryCode: null,
  selectedLevel1: null,
  selectedLevel2: null,
  showEditSurveyInstanceWindowBool: false,
  selectedSurveyInstanceId: null,
  selectedSurveyInstanceNum: null,
  siString: null,

  form: function() {
    if (FLOW.selectedControl.get('selectedSurvey')) {
      return FLOW.selectedControl.get('selectedSurvey');
    }
  }.property('FLOW.selectedControl.selectedSurvey'),

  init: function () {
    this._super();
    FLOW.selectedControl.set('selectedSurveyGroup', null);
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.dateControl.set('toDate', null);
    FLOW.dateControl.set('fromDate', null);
    FLOW.surveyInstanceControl.set('pageNumber', 0);
    FLOW.surveyInstanceControl.set('currentContents', null);
    FLOW.locationControl.set('selectedLevel1', null);
    FLOW.locationControl.set('selectedLevel2', null);
  },
  
  // do a new query
  doFindSurveyInstances: function () {
    FLOW.surveyInstanceControl.get('sinceArray').clear();
    FLOW.surveyInstanceControl.set('pageNumber', -1);
    FLOW.metaControl.set('since', null);
    this.doNextPage();
  },

  doInstanceQuery: function () {
    this.set('beginDate', Date.parse(FLOW.dateControl.get('fromDate')));
    // we add 24 hours to the date, in order to make the date search inclusive.
    dayInMilliseconds = 24 * 60 * 60 * 1000;
    this.set('endDate', Date.parse(FLOW.dateControl.get('toDate')) + dayInMilliseconds);

    // we shouldn't be sending NaN
    if (isNaN(this.get('beginDate'))) {
      this.set('beginDate', null);
    }
    if (isNaN(this.get('endDate'))) {
      this.set('endDate', null);
    }

    if (FLOW.selectedControl.get('selectedSurvey')) {
      this.set('surveyId', FLOW.selectedControl.selectedSurvey.get('keyId'));
    } else {
      this.set('surveyId', null);
    }

    // if we have selected a survey, preload the questions as we'll need them
    // the questions are also loaded once the surveyInstances come in.
    if (FLOW.selectedControl.get('selectedSurvey')) {
      FLOW.questionControl.populateAllQuestions(FLOW.selectedControl.selectedSurvey.get('keyId'));
    }

    if (!Ember.none(FLOW.locationControl.get('selectedCountry'))) {
      this.set('selectedCountryCode',FLOW.locationControl.selectedCountry.get('iso'));
    } else {
      this.set('selectedCountryCode',null);
    }

    if (!Ember.none(FLOW.locationControl.get('selectedLevel1'))) {
      this.set('selectedLevel1',FLOW.locationControl.selectedLevel1.get('name'));
    } else {
      this.set('selectedLevel1',null);
    }

    if (!Ember.none(FLOW.locationControl.get('selectedLevel2'))) {
      this.set('selectedLevel2',FLOW.locationControl.selectedLevel2.get('name'));
    } else {
      this.set('selectedLevel2',null);
    }

    FLOW.surveyInstanceControl.doInstanceQuery(
      this.get('surveyInstanceId'),
      this.get('surveyId'),
      this.get('deviceId'),
      this.get('since'),
      this.get('beginDate'),
      this.get('endDate'),
      this.get('submitterName'),
      this.get('selectedCountryCode'),
      this.get('selectedLevel1'),
      this.get('selectedLevel2'));
  },

  doNextPage: function () {
    var cursorArray, cursor;
    cursorArray = FLOW.surveyInstanceControl.get('sinceArray');
    cursor = cursorArray.length > 0 ? cursorArray[cursorArray.length - 1] : null;
    this.set('since', cursor);
    this.doInstanceQuery();
    FLOW.surveyInstanceControl.set('pageNumber', FLOW.surveyInstanceControl.get('pageNumber') + 1);
  },

  doPrevPage: function () {
    var cursorArray, cursor;
    cursorArray = FLOW.surveyInstanceControl.get('sinceArray');
    cursor = cursorArray.length - 3 > -1 ? cursorArray[cursorArray.length - 3] : null;
    this.set('since', cursor);
    this.doInstanceQuery();
    FLOW.surveyInstanceControl.set('pageNumber', FLOW.surveyInstanceControl.get('pageNumber') - 1);
  },

  // If the number of items in the previous call was 20 (a full page) we assume that there are more.
  // This is not foolproof, but will only lead to an empty next page in 1/20 of the cases
  hasNextPage: function () {
    return FLOW.metaControl.get('numSILoaded') == 20;
  }.property('FLOW.metaControl.numSILoaded'),

  // not perfect yet, sometimes previous link is shown while there are no previous pages.
  hasPrevPage: function () {
    return FLOW.surveyInstanceControl.get('pageNumber');
  }.property('FLOW.surveyInstanceControl.pageNumber'),

  createSurveyInstanceString: function () {
    var si;
    si = FLOW.store.find(FLOW.SurveyInstance, this.get('selectedSurveyInstanceId'));
    this.set('siString', si.get('surveyCode') + "/" + si.get('keyId') + "/" + si.get('submitterName'));
  },

  downloadQuestionsIfNeeded: function () {
    var si, surveyId;
    si = FLOW.store.find(FLOW.SurveyInstance, this.get('selectedSurveyInstanceId'));
    if (!Ember.none(si)) {
      surveyId = si.get('surveyId');
      // if we haven't loaded the questions of this survey yet, do so.
      if (this.get('alreadyLoaded').indexOf(surveyId) == -1) {
        FLOW.questionControl.doSurveyIdQuery(surveyId);
        this.get('alreadyLoaded').push(surveyId);
      }
    }
  },

  // Survey instance edit popup window
  // TODO solve when popup is open, no new surveyIdQuery is done
  showEditSurveyInstanceWindow: function (event) {
    FLOW.questionAnswerControl.doQuestionAnswerQuery(event.context);
    this.get('alreadyLoaded').push(event.context.get('surveyId'));
    this.set('selectedSurveyInstanceId', event.context.get('keyId'));
    this.set('selectedSurveyInstanceNum', event.context.clientId);
    this.set('showEditSurveyInstanceWindowBool', true);
    this.createSurveyInstanceString();
  },

  showEditResponseLink: function () {
    return FLOW.permControl.canEditResponses(this.get('form'));
  }.property('this.form'),

  doCloseEditSIWindow: function (event) {
    this.set('showEditSurveyInstanceWindowBool', false);
  },

  doPreviousSI: function (event) {
    var currentSIList, SIindex, nextItem, filtered, nextSIkeyId, si;
    currentSIList = FLOW.surveyInstanceControl.content.get('content');
    SIindex = currentSIList.indexOf(this.get('selectedSurveyInstanceNum'));

    if (SIindex === 0) {
      // if at the end of the list, go and get more data
    } else {
      nextItem = currentSIList.objectAt(SIindex - 1);
      filtered = FLOW.store.filter(FLOW.SurveyInstance, function (item) {
        if (item.clientId == nextItem) {
          return true;
        } else {
          return false;
        }
      });
      var nextSI = filtered.objectAt(0);
      nextSIkeyId = nextSI.get('keyId');
      this.set('selectedSurveyInstanceId', nextSIkeyId);
      this.set('selectedSurveyInstanceNum', nextItem);
      this.createSurveyInstanceString();
      this.downloadQuestionsIfNeeded();
      FLOW.questionAnswerControl.doQuestionAnswerQuery(nextSI);
    }
  },

  // TODO error checking
  doNextSI: function (event) {
    var currentSIList, SIindex, nextItem, filtered, nextSIkeyId;
    currentSIList = FLOW.surveyInstanceControl.content.get('content');
    SIindex = currentSIList.indexOf(this.get('selectedSurveyInstanceNum'));

    if (SIindex == 19) {
      // TODO get more data
      // if at the end of the list, we should first go back and get more data
    } else {
      nextItem = currentSIList.objectAt(SIindex + 1);
      filtered = FLOW.store.filter(FLOW.SurveyInstance, function (item) {
        if (item.clientId == nextItem) {
          return true;
        } else {
          return false;
        }
      });
      var nextSI = filtered.objectAt(0);
      nextSIkeyId = nextSI.get('keyId');
      this.set('selectedSurveyInstanceId', nextSIkeyId);
      this.set('selectedSurveyInstanceNum', nextItem);
      this.createSurveyInstanceString();
      this.downloadQuestionsIfNeeded();
      FLOW.questionAnswerControl.doQuestionAnswerQuery(nextSI);
    }
  },

  showSurveyInstanceDeleteButton: function() {
    var permissions = FLOW.surveyControl.get('currentFormPermissions');
    return permissions.indexOf("DATA_DELETE") >= 0;
  }.property('FLOW.selectedControl.selectedSurvey'),

  doShowDeleteSIDialog: function (event) {
    FLOW.dialogControl.set('activeAction', 'delSI');
    FLOW.dialogControl.set('showCANCEL', true);
    FLOW.dialogControl.set('showDialog', true);
  },

  deleteSI: function (event) {
    var SI, SIid;
    SIid = this.get('selectedSurveyInstanceId');
    SI = FLOW.store.find(FLOW.SurveyInstance, SIid);
    if (SI !== null) {
      // remove from displayed content
      SI.deleteRecord();
      FLOW.store.commit();
    }
    this.set('showEditSurveyInstanceWindowBool', false);
  },

  validSurveyInstanceId: function() {
    return this.surveyInstanceId === null ||
      this.surveyInstanceId === "" ||
      this.surveyInstanceId.match(/^\d+$/);
  }.property('this.surveyInstanceId'),

  noResults: function() {
    var content = FLOW.surveyInstanceControl.get('content');
    if (content && content.get('isLoaded')) {
      return content.get('length') === 0;
    } else {
      return false;
    }
  }.property('FLOW.surveyInstanceControl.content', 'FLOW.surveyInstanceControl.content.isLoaded'),
  
  //clearing the SI records when the user navigates away from inspect-tab.
  willDestroyElement: function () {
     FLOW.surveyInstanceControl.set('currentContents', null);
     FLOW.metaControl.set('numSILoaded', null);
     FLOW.surveyInstanceControl.set('pageNumber', 0);     
  }
  
});

FLOW.DataItemView = FLOW.View.extend({
  tagName: 'span',
  deleteSI: function () {
    var SI,slKey, sl;
    SI = FLOW.store.find(FLOW.SurveyInstance, this.content.get('keyId'));
    if (SI !== null) {
        // check if we also have the data point loaded locally
        slKey = SI.get('surveyedLocaleId');
        SL = FLOW.store.filter(FLOW.SurveyedLocale,function(item){
            return item.get('keyId') == slKey;
        });
        // if we have found the surveyedLocale, check if there are more
        // formInstances inside it
        if (!Ember.empty(SL)){
            // are there any other formInstances loaded for this surveyedLocale?
            // if not, we also need to not show the locale any more.
            // it will also be deleted automatically in the backend,
            // so this is just to not show it in the UI
            SiList = FLOW.store.filter(FLOW.SurveyInstance,function(item){
                return item.get('surveyedLocaleId') == slKey;
            });
            if (SiList.get('content').length == 1) {
                // this is the only formInstance, so the surveyedLocale
                // will be deleted by the backend, and we need to remove
                // it from the UI
                FLOW.router.surveyedLocaleController.removeLocale(SL.objectAt(0));
            }
        }

      FLOW.surveyInstanceControl.removeInstance(SI);
      SI.deleteRecord();
      FLOW.store.commit();
    }
  }
});

FLOW.DataLocaleItemView = FLOW.View.extend({
    tagName: 'span',
    deleteSL: function () {
        var SL;
        SL = FLOW.store.find(FLOW.SurveyedLocale, this.content.get('keyId'));
        if (SL !== null){
            FLOW.router.surveyedLocaleController.removeLocale(SL);
            // the filled forms inside this data point will be deleted by the backend
            SL.deleteRecord();
            FLOW.store.commit();
            }
        }
});

FLOW.DataNumView = FLOW.View.extend({
  tagName: 'span',
  pageNumber: 0,
  content: null,
  rownum: function () {
     return this.get("_parentView.contentIndex") + 1 + 20 * this.get('pageNumber');
  }.property(),
});

});

loader.register('akvo-flow/views/data/monitoring-data-table-view', function(require) {
FLOW.MonitoringDataTableView = FLOW.View.extend({
  showingDetailsDialog: false,
  cursorStart: null,

  pageNumber: function(){
	return FLOW.router.surveyedLocaleController.get('pageNumber');
  }.property('FLOW.router.surveyedLocaleController.pageNumber'),

  showDetailsDialog: function (evt) {
	FLOW.surveyInstanceControl.set('content', FLOW.store.findQuery(FLOW.SurveyInstance, {
		surveyedLocaleId: evt.context.get('keyId')
	}));
    this.toggleProperty('showingDetailsDialog');
  },

  showApprovalStatusColumn: function () {
      return FLOW.Env.enableDataApproval;
  }.property(),

  closeDetailsDialog: function () {
    this.toggleProperty('showingDetailsDialog');
  },

  showSurveyInstanceDetails: function (evt) {
    FLOW.questionAnswerControl.doQuestionAnswerQuery(evt.context);
    $('.si_details').hide();
    $('tr[data-flow-id="si_details_' + evt.context.get('keyId') + '"]').show();
  },

  findSurveyedLocale: function (evt) {
	  var ident = this.get('identifier'),
	      displayName = this.get('displayName'),
	      sgId = FLOW.selectedControl.get('selectedSurveyGroup'),
	      cursorType = FLOW.metaControl.get('cursorType'),
        criteria = {};

	  if (ident) {
		  criteria.identifier = ident;
	  }

	  if (displayName) {
		  criteria.displayName = displayName;
	  }

	  if (sgId) {
		  criteria.surveyGroupId = sgId.get('keyId');
	  }

	  if (this.get('cursorStart')) {
		criteria.since = this.get('cursorStart');
	  }
      var surveyedLocaleController = FLOW.router.get('surveyedLocaleController');
      surveyedLocaleController.populate(criteria);

      surveyedLocaleController.get('content').on('didLoad', function () {
          var surveyedLocales = this;
          var surveyedLocaleIds = Ember.A();
          surveyedLocales.forEach(function (item) {
              surveyedLocaleIds.addObject(item.get('keyId'));
          })
          FLOW.router.dataPointApprovalController.loadBySurveyedLocaleId(surveyedLocaleIds);
      })

      if(Ember.empty(FLOW.router.userListController.get('content'))) {
          FLOW.router.userListController.set('content', FLOW.User.find());
      }
  },
  
  noResults: function () {
    var content = FLOW.router.surveyedLocaleController.get('content');
    if (content && content.get('isLoaded')) {
        return content.get('length') === 0;
    }
  }.property('FLOW.router.surveyedLocaleController.content','FLOW.router.surveyedLocaleController.content.isLoaded'),

  doNextPage: function () {
	var cursorArray, cursorStart;
	cursorArray = FLOW.router.surveyedLocaleController.get('sinceArray');
	cursorStart = cursorArray.length > 0 ? cursorArray[cursorArray.length - 1] : null;
	this.set('cursorStart', cursorStart);
    this.findSurveyedLocale();
    FLOW.router.surveyedLocaleController.set('pageNumber', FLOW.router.surveyedLocaleController.get('pageNumber') + 1);
  },

  doPrevPage: function () {
	var cursorArray, cursorStart;
	cursorArray = FLOW.router.surveyedLocaleController.get('sinceArray');
	cursorStart = cursorArray.length - 3 > -1 ? cursorArray[cursorArray.length - 3] : null;
	this.set('cursorStart', cursorStart);
    this.findSurveyedLocale();
    FLOW.router.surveyedLocaleController.set('pageNumber', FLOW.router.surveyedLocaleController.get('pageNumber') - 1);
  },

  hasNextPage: function () {
    return FLOW.metaControl.get('numSLLoaded') == 20;
  }.property('FLOW.metaControl.numSLLoaded'),

  hasPrevPage: function () {
    return FLOW.router.surveyedLocaleController.get('pageNumber');
  }.property('FLOW.router.surveyedLocaleController.pageNumber'),
  
  willDestroyElement: function () {
    FLOW.router.surveyedLocaleController.set('currentContents', null);
    FLOW.metaControl.set('numSLLoaded',null)
    FLOW.router.surveyedLocaleController.set('pageNumber',0)
  }
});

/**
 * View of each row/data point in the monitoring data tab
 */
FLOW.DataPointView = FLOW.View.extend({
    templateName: 'navData/monitoring-data-row',

    approvalStatus: [{label: Ember.String.loc('_pending'), value: 'PENDING'}, { label: Ember.String.loc('_approved'), value: 'APPROVED' },{ label: Ember.String.loc('_rejected'), value: 'REJECTED'}],
     
     //catering for counter for the data points.
    tagName: 'span',
    content: null,
    pageNumber: 0,
    showDataApprovalBlock: false,

    showApprovalStatusColumn: function () {
        return this.get('parentView').get('showApprovalStatusColumn');
    }.property(),

    dataPointApprovals: function () {
        var approvals = FLOW.router.dataPointApprovalController.get('content');
        if(!approvals) {
            return;
        }

        var surveyedLocaleId = this.content && this.content.get('keyId');
        return approvals.filterProperty('surveyedLocaleId', surveyedLocaleId);
    }.property('FLOW.router.dataPointApprovalController.content.@each'),

    /*
     * get the next approval step id
     */
    nextApprovalStepId: function () {
        return this.get('nextApprovalStep') && this.get('nextApprovalStep').get('keyId');
    }.property('this.nextApprovalStep'),

    /*
     * Derive the next approval step (in ordered approvals)
     */
    nextApprovalStep: function () {
        var nextStep;
        var approvals = this.get('dataPointApprovals');
        var steps = FLOW.router.approvalStepsController.get('arrangedContent');

        if (Ember.empty(approvals)) {
            return steps && steps.get('firstObject');
        }

        steps.forEach(function (step) {
            var approval = approvals.filterProperty('approvalStepId',
                                        step.get('keyId')).get('firstObject');
            var isPendingStep = !approval || approval.get('status') === 'PENDING';
            var isRejectedStep = approval && approval.get('status') === 'REJECTED';
            if (!nextStep && (isPendingStep || isRejectedStep)) {
                nextStep = step;
            }
        });

        return nextStep;

    // NOTE: below we observe the '@each.approvalDate' in order to be
    // sure that we only recalculate the next step whenever the approval
    //  has been correctly updated on the server side
    }.property('this.dataPointApprovals.@each.approvalDate'),

    /*
     * return true if there are any of the approvals rejected in this set
     */
    hasRejectedApproval: function () {
        var approvals = this.get('dataPointApprovals');
        return !Ember.empty(approvals.filterProperty('status', 'REJECTED'));
    }.property('this.dataPointApprovals'),

    loadDataPointApprovalObserver: function () {
        if(!this.get('showDataApprovalBlock')) {
            return; // do nothing when hiding approval block
        }

        var dataPoint = this.get('content');
        if (dataPoint) {
            FLOW.router.dataPointApprovalController.loadBySurveyedLocaleId(dataPoint.get('keyId'));
        }
    }.observes('this.showDataApprovalBlock'),

    toggleShowDataApprovalBlock: function () {
        this.toggleProperty('showDataApprovalBlock');
    },

    dataPointRowNumber: function () {
        var pageNumber = FLOW.router.surveyedLocaleController.get('pageNumber');
        return this.get('_parentView.contentIndex') + 1 + 20 * pageNumber;
    }.property()
});

/**
 * View to render the status of a data point in the approval
 * status cell of each data point / row
 */
FLOW.DataPointApprovalStatusView = FLOW.View.extend({
    content: null,

    dataPointApprovalStatus: function () {
        var approvalStepStatus;
        var latestApprovalStep = this.get('latestApprovalStep');
        if (!latestApprovalStep) {
            return;
        }

        var dataPointApprovals = this.get('parentView').get('dataPointApprovals');
        var dataPointApproval = dataPointApprovals &&
                                    dataPointApprovals.filterProperty('approvalStepId',
                                            latestApprovalStep.get('keyId')).get('firstObject');
        approvalStepStatus = dataPointApproval && dataPointApproval.get('status') ||
                                Ember.String.loc('_pending');

        return latestApprovalStep.get('title') + ' - ' + approvalStepStatus.toUpperCase();
    }.property('this.parentView.nextApprovalStep'),

    /*
     * Derive the latest approval step for a particular data point
     */
    latestApprovalStep: function () {
        var lastStep, steps;
        var nextStep = this.get('parentView').get('nextApprovalStep');

        if (nextStep) {
            return nextStep;
        } else {
            steps = FLOW.router.approvalStepsController.get('arrangedContent');
            lastStep = steps && steps.get('lastObject');
            return lastStep;
        }
    }.property('this.parentView.nextApprovalStep'),
});

/**
 * The data approval view for each approval step of a data point
 */
FLOW.DataPointApprovalView = FLOW.View.extend({
    step: null,

    dataPoint: null,

    dataPointApproval: function () {
        var approvals = this.get('parentView').get('dataPointApprovals');
        var defaultApproval = Ember.Object.create({ status: null, comment: null});

        if(Ember.empty(approvals)) {
            return defaultApproval;
        }

        var stepId = this.step && this.step.get('keyId');
        var approval = approvals.filterProperty('approvalStepId', stepId).get('firstObject');

        return approval || defaultApproval;
    }.property('this.parentView.dataPointApprovals'),

    isApprovedStep: function () {
        var dataPointApproval = this.get('dataPointApproval');
        return dataPointApproval && dataPointApproval.get('keyId');
    }.property('this.dataPointApproval'),

    /*
     * return the current user's id
     */
    currentUserId: function () {
        var step = this.get('step');
        var currentUserEmail = FLOW.currentUser.get('email');
        var userList = FLOW.router.userListController.get('content');
        var currentUser = userList &&
                            userList.filterProperty('emailAddress', currentUserEmail).get('firstObject');
        return currentUser && currentUser.get('keyId');
    }.property(),

    /*
     * Enable the approval fields based on whether or not approval steps
     * should be executed in order
     */
    showApprovalFields: function () {
        if(this.get('parentView').get('hasRejectedApproval')) {
            return false;
        }

        var approvalGroup = FLOW.router.approvalGroupController.get('content');
        var currentUserId = this.get('currentUserId');
        if(approvalGroup && approvalGroup.get('ordered')) {
            var nextStep = this.get('parentView').get('nextApprovalStep');
            if (nextStep) {
                return this.step.get('keyId') === nextStep.get('keyId') &&
                    nextStep.get('approverUserList') &&
                    nextStep.get('approverUserList').contains(currentUserId);
            } else {
                return false;
            }
        } else {
            // this is for unordered approval steps. show fields for
            // all steps so that its possible to approve any step
            return true;
        }
    }.property('this.parentView.nextApprovalStep'),

    /*
     *  Submit data approval properties to controller
     */
    submitDataPointApproval: function (event) {

        var dataPointApproval = this.get('dataPointApproval');
        if(dataPointApproval.get('keyId')) {
            FLOW.router.dataPointApprovalController.update(dataPointApproval);
        } else {
            dataPointApproval.surveyedLocaleId = this.get('dataPoint').get('keyId');
            dataPointApproval.approvalStepId = this.get('step').get('keyId');
            dataPointApproval.approverUserName = null;

            FLOW.router.dataPointApprovalController.add(dataPointApproval);
        }
    },
});
});

loader.register('akvo-flow/views/data/question-answer-view', function(require) {
// this function is also present in assignment-edit-views.js, we need to consolidate using moment.js

function formatDate(date) {
  if (date && !isNaN(date.getTime())) {
    return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
  }
  return null;
}

function sortByOrder(a , b) {
  return a.get('order') - b.get('order');
}

FLOW.QuestionAnswerView = Ember.View.extend({

  isTextType: function(){
    return this.get('questionType') === 'FREE_TEXT';
  }.property('this.questionType'),

  isCascadeType: function(){
    return this.get('questionType') === 'CASCADE';
  }.property('this.questionType'),

  isOptionType: function(){
    return this.get('questionType') === 'OPTION';
  }.property('this.questionType'),

  isNumberType: function(){
    return this.get('questionType') === 'NUMBER';
  }.property('this.questionType'),

  isBarcodeType: function(){
    return this.get('questionType') === 'SCAN';
  }.property('this.questionType'),

  isDateType: function(){
    return this.get('questionType') === 'DATE';
  }.property('this.questionType'),

  isPhotoType: function(){
    return (this.get('questionType') === 'PHOTO' || (this.content && this.content.get('type') === 'IMAGE'));
  }.property('this.questionType'),

  isVideoType: function(){
    return this.get('questionType') === 'VIDEO';
  }.property('this.questionType'),

  isGeoShapeType: function(){
    return this.get('questionType') === 'GEOSHAPE';
  }.property('this.questionType'),

  isSignatureType: function(){
    return this.get('questionType') === 'SIGNATURE' || (this.content && this.content.get('type') === 'SIGNATURE');
  }.property('this.questionType'),

  isCaddisflyType: function(){
    return this.get('questionType') === 'CADDISFLY' || (this.content && this.content.get('type') === 'CADDISFLY');
  }.property('this.questionType'),

  nonEditableQuestionTypes: ['GEO', 'PHOTO', 'VIDEO', 'GEOSHAPE', 'SIGNATURE','CADDISFLY'],

  form: function() {
    if (FLOW.selectedControl.get('selectedSurvey')) {
      return FLOW.selectedControl.get('selectedSurvey');
    }
  }.property('FLOW.selectedControl.selectedSurvey'),

  /*
   * Get the full list of options related to a particular option type question
   */
  optionsList: function(){
    var c = this.content;
    if (Ember.none(c) || !this.get('isOptionType')) {
      return Ember.A([]);
    }

    var questionId = c.get('questionID');

    var options = FLOW.store.filter(FLOW.QuestionOption, function (item) {
      return item.get('questionId') === +questionId;
    });

    optionArray = options.toArray();
    optionArray.sort(function (a, b) {
        return a.get('order') - b.get('order');
    });

    tempList = Ember.A([]);
    var obj;
    optionArray.forEach(function (item) {
      obj = Ember.Object.create({
        code : item.get('code') && item.get('code').trim(),
        text : item.get('text').trim(),
        order: item.get('order')
      });
      tempList.push(obj);
    });

    // add other option if enabed
    // we assume codes are all or nothing
    var setOptionCodes = tempList.get('firstObject').get('code');
    if (this.get('isOtherOptionEnabled')) {
      tempList.push(Ember.Object.create({
        code: setOptionCodes ? "OTHER" : null, // OTHER is default code
        otherText: null,
        text: function () {
          var suffix = this.get('otherText') ? this.get('otherText') : Ember.String.loc('_other_option_specify');
          return Ember.String.loc('_other') + ": " + suffix;
        }.property('this.otherText'),
        order: tempList.get('length'),
        isOther: true,
      }));
    }
    return tempList;
  }.property('this.content'),

  content: null,

  inEditMode: false,

  isNotEditable: function() {
    // keep this property to limit template rafactoring
    return !this.get('isEditable');
  }.property('this.isEditable'),

  isEditable: function () {
    var isEditableQuestionType, canEditFormResponses;
    isEditableQuestionType = this.nonEditableQuestionTypes.indexOf(this.get('questionType')) < 0;
    if (!isEditableQuestionType) {
      return false; // no need to check permissions
    }
    canEditFormResponses = FLOW.permControl.canEditResponses(this.get('form'));
    return isEditableQuestionType && canEditFormResponses;
  }.property('this.questionType,this.form'),

  date: null,

  /*
   *  Extract base64 signature image data and convert to src attribute for
   *  HTML img tag
   */
  signatureImageSrc: function () {
    var c = this.content, srcAttr = 'data:image/png;base64,', signatureJson;
    if (c && c.get('value')) {
      signatureJson = JSON.parse(c.get('value'));
      return srcAttr + signatureJson.image;
    }
    return null
  }.property('this.content'),

  /*
   * Extract signatory from signature response
   */
  signatureSignatory: function () {
    var c = this.content, signatureJson;
    if (c && c.get('value')) {
      signatureJson = JSON.parse(c.get('value'));
      return signatureJson.name.trim();
    }
    return null;
  }.property('this.content'),

  /*
  * Parse the caddisfly test JSON result
  * Extracts the 'result' key from a json string, and parses the array of results.
  * Creates an array with name, value, unit for each result
  * Example JSON format: {"result":[{"name":"Total Chlorine (ppm)","value":10,
  * "unit":"ppm","id":0},{"name":"Free Chlorine (ppm)","value":0.5,"unit":"ppm",
  * "id":1}],"type":"caddisfly","name":"Chlorine and Free Chlorine",
  * "uuid":"bf1c19c0-9788-4e26-999e-1b5c6ca28111","image":"b3893f16-6a02-4e92-a13e-fce25223a0c5.png"}
  */
  parseTestJson: function(){
    var c = this.content, testJson, newResult, image, result;
    result = Ember.A();
    if (c && c.get('value')) {
      testJson = JSON.parse(c.get('value'));
      if (testJson.result && !Ember.empty(testJson.result)){
          result = Ember.A(testJson.result);
      }
    }
    this.set('testResult',result);
  },

  /*
   * Get out the caddisfly test name
   *
   * Example JSON format: {"result":[{"name":"Total Chlorine (ppm)","value":10,
   * "unit":"ppm","id":0},{"name":"Free Chlorine (ppm)","value":0.5,"unit":"ppm",
   * "id":1}],"type":"caddisfly","name":"Chlorine and Free Chlorine",
   * "uuid":"bf1c19c0-9788-4e26-999e-1b5c6ca28111","image":"b3893f16-6a02-4e92-a13e-fce25223a0c5.png"}
   *
   * Extracts the 'name' attribute from a Caddisfly JSON result string
   */
  testName: function(){
    var c = this.content, testJson;
    if (c && c.get('value')) {
      testJson = JSON.parse(c.get('value'));
      if (!Ember.empty(testJson.result)){
          this.parseTestJson();
      }
      if (!Ember.empty(testJson.name)){
          return testJson.name.trim();
      }
    }
    return null;
  }.property('this.content'),

  /*
   * Get out the caddisfly image URL
   *
   * Example JSON format: {"result":[{"name":"Total Chlorine (ppm)","value":10,
   * "unit":"ppm","id":0},{"name":"Free Chlorine (ppm)","value":0.5,"unit":"ppm",
   * "id":1}],"type":"caddisfly","name":"Chlorine and Free Chlorine",
   * "uuid":"bf1c19c0-9788-4e26-999e-1b5c6ca28111","image":"b3893f16-6a02-4e92-a13e-fce25223a0c5.png"}
   *
   * Extracts the 'image' attribute from a Caddisfly JSON result string, and returns a full URL
   */
  caddisflyImageURL: function(){
    var c = this.content, testJson;
    if (c && c.get('value')) {
      testJson = JSON.parse(c.get('value'));
      if (!Ember.empty(testJson.image)){
        return FLOW.Env.photo_url_root + testJson.image.trim();
      }
    }
    return null;
  }.property('this.content'),

  numberValue: null,

  cascadeValue: function(key, value, previousValue){
    var c = this.content;
    // setter
    if (arguments.length > 1) {
      // split according to pipes
      var cascadeNames = value.split("|");
      var cascadeResponse = [];
      var obj = null;
      cascadeNames.forEach(function(item){
        if (item.trim().length > 0) {
          obj = {};
          obj.name = item.trim();
          cascadeResponse.push(obj);
        }
      });

      c.set('value', JSON.stringify(cascadeResponse));
    }

    // getter
    var cascadeString = "", cascadeJson;
    if (c && c.get('value')) {
      if (c.get('value').indexOf("|") > -1) {
        cascadeString += c.get('value');
      } else {
        cascadeJson = JSON.parse(c.get('value'));
        cascadeString = cascadeJson.map(function(item){
          return item.name;
        }).join("|");
      }
      return cascadeString;
    }
    return null;
  }.property('this.content'),

  /* object properties to include when transforming selected options
   * to string to store in the datastore
   */
  optionValueProperties: ['code', 'text', 'isOther'],

  /*
   *  An Ember array consisting of selected elements from the optionsList.
   *  This is later serialised into a string response for the datastore.
   */
  selectedOptionValues: null,

  /*
   *  A view property to set and retrieve the selectedOptionValues array
   *
   *  At first load, the getter block uses the string response from the datastore
   *  to create an Ember array consisting of the corresponding elements from the
   *  optionsList property, i.e, selected elements of the optionsList.
   *
   *  Retrieved string responses could be:
   *   - pipe-separated strings for legacy format e.g. 'text1|text2'
   *   - JSON string in the current format e.g
   *    '[{text: "text with code", code: "code"}]'
   *    '[{text: "only text"}]'
   */
  optionValue: function (key, value, previousValue) {
    var valueArray = [], selectedOptions = Ember.A(), c = this.content, isOtherEnabled;

    // setter
    if (arguments.length > 1) {
      this.set('selectedOptionValues', value);
    }

    // getter
    // initial selection setup
    if (!this.get('selectedOptionValues')) {
      this.set('selectedOptionValues', this.parseOptionsValueString(c.get('value')));
    }
    return this.get('selectedOptionValues');
  }.property('this.selectedOptionValues'),

  /*
   *  Used to parse a provided string response from the value property of an option question.
   *  Returns an Ember array consisting of the corresponding elements from the optionsList
   *  property, i.e, selected elements of the optionsList.
   *
   *  The string responses could be:
   *   - pipe-separated strings for legacy format e.g. 'text1|text2'
   *   - JSON string in the current format e.g
   *    '[{text: "text with code", code: "code"}]'
   *    '[{text: "only text"}]'
   */
  parseOptionsValueString: function (optionsValueString) {
    if (!optionsValueString) {
      return Ember.A();
    }

    var selectedOptions = Ember.A();
    var optionsList = this.get('optionsList');
    var isOtherEnabled = this.get('isOtherOptionEnabled');

    if (optionsValueString.charAt(0) === '[') {
      // responses in JSON format
      JSON.parse(optionsValueString).forEach(function (response) {
        optionsList.forEach(function (optionObj) {
          if (response.text === optionObj.get('text') &&
              response.code == optionObj.get('code')) { // '==' because codes could be undefined or null
            selectedOptions.addObject(optionObj);
          }

          // add other
          if (response.isOther && optionObj.get('isOther') && isOtherEnabled) {
            optionObj.set('otherText', response.text);
            selectedOptions.addObject(optionObj);
          }
        });
      });
    } else {
      // responses in pipe separated format
      optionsValueString.split("|").forEach(function(item, textIndex, textArray){
        var text = item.trim(), isLastItem = textIndex === textArray.length - 1;
        if (text.length > 0) {
          optionsList.forEach(function(optionObj) {
            var optionIsIncluded = optionObj.get('text') && optionObj.get('text') === text;
            if (optionIsIncluded) {
              selectedOptions.addObject(optionObj);
            }

            // add other
            if (!optionIsIncluded && optionObj.get('isOther') && isOtherEnabled && isLastItem) {
              optionObj.set('otherText', text);
              selectedOptions.addObject(optionObj);
            }
          });
        }
      });
    }

    return selectedOptions.sort(sortByOrder);
  },

  /*
   *  A property to enable setting and getting of the selected element
   *  of a single-select option question.
   *
   */
  singleSelectOptionValue: function (key, value, previousValue) {
    var selectedOptions, c = this.content;

    // setter
    if (c && arguments.length > 1) {
      selectedOptions = Ember.A();
      selectedOptions.push(value);
      this.set('optionValue', selectedOptions);
    }

    // getter
    var options = this.get('optionValue');
    if (options && options.get('length') === 1) {
      return options.get('firstObject');
    }
    return null;
  }.property('this.optionValue'),

  /*
   *  A property to enable setting and getting of the selected element
   *  of a multi-select option question.
   *
   */
  multiSelectOptionValue: function (key, value, previousValue) {
    var c = this.content;

    // setter
    if (c && arguments.length > 1) {
      this.set('optionValue', value);
    }

    // getter
    return this.get('optionValue');
  }.property('this.optionValue'),

  isMultipleSelectOption: function () {
    return this.get('isOptionType') && this.get('question').get('allowMultipleFlag');
  }.property('this.isOptionType'),

  isOtherOptionEnabled: function () {
    return this.get('isOptionType') && this.get('question').get('allowOtherFlag');
  }.property('this.isOptionType'),

  isOtherOptionSelected: function () {
    var selectedOption = this.get('optionValue') && this.get('optionValue').get('lastObject');
    return selectedOption && selectedOption.get('isOther');
  }.property('this.optionValue'),

  photoUrl: function(){
    var c = this.content;
    if (!Ember.empty(c.get('value'))) {
      jImage = JSON.parse(c.get('value'));
      if (jImage && jImage.filename) {
          return FLOW.Env.photo_url_root + jImage.filename.split('/').pop();
      }
    }
  }.property('this.content,this.isPhotoType,this.isVideoType'),

  photoLocation: function(){
    var c = this.content;
    if (!Ember.empty(c.get('value'))) {
      jImage = JSON.parse(c.get('value'));
      if (jImage && jImage.location) {
          return "lat:" + jImage.location.latitude + "/lon:" + jImage.location.longitude;
      }
    }
  }.property('this.content,this.isPhotoType,this.isVideoType'),

  geoShapeObject: function(){
    var c = this.content;
    if (!Ember.empty(c.get('value'))) {
      return c.get('value');
    }
  }.property('this.content,this.isGeoShapeType'),

  questionType: function(){
    if(this.get('question')){
      return this.get('question').get('type');
    }
  }.property('this.question'),

  question: function(){
    var c = this.get('content');
    if (c) {
      var questionId = c.get('questionID');
      var q = FLOW.questionControl.findProperty('keyId', +questionId);
      return q;
    }
  }.property('this.content'),

  doEdit: function () {
    this.set('inEditMode', true);
    var c = this.content;

    if (this.get('isDateType') && !Ember.empty(c.get('value'))) {
      var d = new Date(+c.get('value')); // need to coerce c.get('value') due to milliseconds
      this.set('date', formatDate(d));
    }

    if (this.get('isNumberType') && !Ember.empty(c.get('value'))) {
      this.set('numberValue', c.get('value'));
    }
  },

  doCancel: function () {
    this.set('inEditMode', false);
  },

  doSave: function () {

    if (this.get('isDateType')) {
      var d = Date.parse(this.get('date'));
      if (isNaN(d) || d < 0) {
        this.content.set('value', null);
      } else {
        this.content.set('value', d);
      }
    }

    if (this.get('isNumberType')) {
      if (isNaN(this.numberValue)) {
        this.content.set('value', null);
      } else {
        this.content.set('value', this.numberValue);
      }
    }

    if (this.get('isOptionType')) {
      var responseArray = [];
      this.get('selectedOptionValues').forEach(function(option){
        var obj = {};
        if (option.get('code')) {
          obj.code = option.get('code');
        }
        if (option.get('isOther')) {
          obj.isOther = option.get('isOther');
          obj.text = option.get('otherText');
        } else {
          obj.text = option.get('text');
        }
        responseArray.push(obj);
      });
      this.content.set('value', JSON.stringify(responseArray));
    }
    FLOW.store.commit();
    this.set('inEditMode', false);

  },

  doValidateNumber: function () {
    // TODO should check for minus sign and decimal point, depending on question setting
    this.set('numberValue', this.get('numberValue').toString().replace(/[^\d.]/g, ""));
  }.observes('this.numberValue')
});

FLOW.QuestionAnswerOptionListView = Ember.CollectionView.extend({
  tagName: 'ul',
  content: null,
  itemViewClass: Ember.View.extend({
    template: Ember.Handlebars.compile("{{view.content.text}}")
  })
});

/**
 * Multi select option editing view that renders question options with the allow
 * multiple option selected.  The selection property should be a set of options
 * that are considered to be selected (checked).  Whenever each options checkbox
 * is modified, the set bound to the selection property is updated to add or remove
 * the item.
 *
 * The inline view template displays the selected option's text with its corresponding
 * checkbox and if the 'allow other' flag is set on the question, it displays in addition
 * a text box to enable editing the other response text.
 */
FLOW.QuestionAnswerMultiOptionEditView = Ember.CollectionView.extend({
  tagName: 'ul',
  content: null,
  selection: null,
  itemViewClass: Ember.View.extend({
    template: Ember.Handlebars.compile('{{view Ember.Checkbox checkedBinding="view.isSelected"}} {{view.content.text}}'),
    isSelected: function(key, checked, previousValue) {
      var selectedOptions = this.get('parentView').get('selection');
      var newSelectedOptions = Ember.A();

      // setter
      if (arguments.length > 1) {
        if (checked) {
          selectedOptions.addObject(this.content);
        } else {
          selectedOptions.removeObject(this.content);
        }
        selectedOptions.forEach(function(option){
          newSelectedOptions.push(option);
        });

        // Using a copy of the selected options 'newSelectedOptions' in order
        // to trigger the setter property of the object bound to 'parentView.selection'
        this.set('parentView.selection', newSelectedOptions);
      }

      // getter
      return selectedOptions && selectedOptions.contains(this.content);
    }.property('this.content'),
  })
});

FLOW.QuestionAnswerInspectDataView = FLOW.QuestionAnswerView.extend({
  templateName: 'navData/question-answer',
});

FLOW.QuestionAnswerMonitorDataView = FLOW.QuestionAnswerView.extend({
  templateName: 'navData/question-answer',
  
  doEdit : function (){ //override the doEdit action in the parentView
    this._super();
    this.set('inEditMode', false)
  }
})

});

loader.register('akvo-flow/views/devices/assignment-edit-views', function(require) {
// removes duplicate objects with a clientId from an Ember Array

FLOW.ArrNoDupe = function (a) {
  var templ, i, item = null,
    gotIt, tempa;
  templ = {};
  tempa = Ember.A([]);
  for (i = 0; i < a.length; i++) {
    templ[a.objectAt(i).clientId] = true;
  }
  for (item in templ) {
    gotIt = false;
    for (i = 0; i < a.length; i++) {
      if (a.objectAt(i).clientId == item && !gotIt) {
        tempa.pushObject(a.objectAt(i));
        gotIt = true;
      }
    }
  }
  return tempa;
};

FLOW.formatDate = function (value) {
  if (!Ember.none(value)) {
    return value.getFullYear() + "/" + (value.getMonth() + 1) + "/" + value.getDate();
  } else return null;
};

FLOW.AssignmentEditView = FLOW.View.extend({
  devicesPreview: Ember.A([]),
  surveysPreview: Ember.A([]),
  assignmentName: null,
  language: null,

  init: function () {
    var deviceIds, previewDevices, surveyIds, previewSurveys, startDate = null,
      endDate = null;
    previewDevices = Ember.A([]);
    previewSurveys = Ember.A([]);
    this._super();
    this.set('assignmentName', FLOW.selectedControl.selectedSurveyAssignment.get('name'));
    FLOW.selectedControl.set('selectedDevices', null);
    FLOW.selectedControl.set('selectedSurveys', null);
    FLOW.selectedControl.set('selectedSurveyGroup', null);
    FLOW.selectedControl.set('selectedDeviceGroup', null);
    FLOW.surveyControl.set('content', null);
    FLOW.devicesInGroupControl.set('content', null);

    if (FLOW.selectedControl.selectedSurveyAssignment.get('startDate') > 0) {
      startDate = new Date(FLOW.selectedControl.selectedSurveyAssignment.get('startDate'));
    }
    if (FLOW.selectedControl.selectedSurveyAssignment.get('endDate') > 0) {
      endDate = new Date(FLOW.selectedControl.selectedSurveyAssignment.get('endDate'));
    }
    FLOW.dateControl.set('fromDate', FLOW.formatDate(startDate));
    FLOW.dateControl.set('toDate', FLOW.formatDate(endDate));

    this.set('language', FLOW.selectedControl.selectedSurveyAssignment.get('language'));

    deviceIds = Ember.A(FLOW.selectedControl.selectedSurveyAssignment.get('devices'));

    deviceIds.forEach(function (item) {
      previewDevices.pushObjects(FLOW.store.find(FLOW.Device, item));
    });
    this.set('devicesPreview', previewDevices);

    surveyIds = Ember.A(FLOW.selectedControl.selectedSurveyAssignment.get('surveys'));

    surveyIds.forEach(function (item) {
      if (item !== null) {
        previewSurveys.pushObjects(FLOW.store.find(FLOW.Survey, item));
      }
    });
    this.set('surveysPreview', previewSurveys);
  },

  detectChangeTab: function () {
    if (Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))) {
      FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
    }
    FLOW.selectedControl.set('selectedSurveyAssignment', null);
  }.observes('FLOW.router.navigationController.selected', 'FLOW.router.devicesSubnavController.selected'),

  assignmentNotComplete: function () {
	  if (Ember.empty(this.get('assignmentName'))) {
		  FLOW.dialogControl.set('activeAction', 'ignore');
		  FLOW.dialogControl.set('header', Ember.String.loc('_assignment_name_not_set'));
		  FLOW.dialogControl.set('message', Ember.String.loc('_assignment_name_not_set_text'));
		  FLOW.dialogControl.set('showCANCEL', false);
		  FLOW.dialogControl.set('showDialog', true);
		  return true;
	  }
	  if (Ember.none(FLOW.dateControl.get('toDate')) || Ember.none(FLOW.dateControl.get('fromDate'))) {
		  FLOW.dialogControl.set('activeAction', 'ignore');
		  FLOW.dialogControl.set('header', Ember.String.loc('_date_not_set'));
		  FLOW.dialogControl.set('message', Ember.String.loc('_date_not_set_text'));
		  FLOW.dialogControl.set('showCANCEL', false);
		  FLOW.dialogControl.set('showDialog', true);
		  return true;
	  }
	  return false;
  },

  saveSurveyAssignment: function () {
    var sa, endDateParse, startDateParse, devices = [],
      surveys = [];
    if (this.assignmentNotComplete()){
		return;
	}
    sa = FLOW.selectedControl.get('selectedSurveyAssignment');
    sa.set('name', this.get('assignmentName'));

    if (!Ember.none(FLOW.dateControl.get('toDate'))) {
      endDateParse = Date.parse(FLOW.dateControl.get('toDate'));
    } else {
      endDateParse = null;
    }

    if (!Ember.none(FLOW.dateControl.get('fromDate'))) {
      startDateParse = Date.parse(FLOW.dateControl.get('fromDate'));
    } else {
      startDateParse = null;
    }

    sa.set('endDate', endDateParse);
    sa.set('startDate', startDateParse);
    sa.set('language', 'en');

    this.get('devicesPreview').forEach(function (item) {
      devices.push(item.get('keyId'));
    });
    sa.set('devices', devices);

    this.get('surveysPreview').forEach(function (item) {
      surveys.push(item.get('keyId'));
    });
    sa.set('surveys', surveys);

    FLOW.store.commit();
    FLOW.router.transitionTo('navDevices.assignSurveysOverview');
  },

  cancelEditSurveyAssignment: function () {
    if (Ember.none(FLOW.selectedControl.selectedSurveyAssignment.get('keyId'))) {
      FLOW.selectedControl.get('selectedSurveyAssignment').deleteRecord();
    }
    FLOW.selectedControl.set('selectedSurveyAssignment', null);
    FLOW.router.transitionTo('navDevices.assignSurveysOverview');
  },

  addSelectedDevices: function () {
    this.devicesPreview.pushObjects(FLOW.selectedControl.get('selectedDevices'));
    // delete duplicates
    this.set('devicesPreview', FLOW.ArrNoDupe(this.get('devicesPreview')));
  },

  addSelectedSurveys: function () {
    var sgName;
    sgName = FLOW.selectedControl.selectedSurveyGroup.get('code');
    FLOW.selectedControl.get('selectedSurveys').forEach(function (item) {
      item.set('surveyGroupName', sgName);
    });
    this.surveysPreview.pushObjects(FLOW.selectedControl.get('selectedSurveys'));
    // delete duplicates
    this.set('surveysPreview', FLOW.ArrNoDupe(this.get('surveysPreview')));
  },

  selectAllDevices: function () {
    var selected = Ember.A([]);
    FLOW.devicesInGroupControl.get('content').forEach(function (item) {
      selected.pushObject(item);
    });
    FLOW.selectedControl.set('selectedDevices', selected);
  },

  deselectAllDevices: function () {
    FLOW.selectedControl.set('selectedDevices', []);
  },

  selectAllSurveys: function () {
	var selected = FLOW.surveyControl.get('content').filter(function (item) {
	    return item.get('status') === "PUBLISHED";
	});
    FLOW.selectedControl.set('selectedSurveys', selected);
  },

  deselectAllSurveys: function () {
    FLOW.selectedControl.set('selectedSurveys', []);
  },

  removeSingleSurvey: function (event) {
    var id, surveysPreview, i;
    id = event.context.get('clientId');
    surveysPreview = this.get('surveysPreview');
    for (i = 0; i < surveysPreview.length; i++) {
      if (surveysPreview.objectAt(i).clientId == id) {
        surveysPreview.removeAt(i);
      }
    }
    this.set('surveysPreview', surveysPreview);
  },

  removeAllSurveys: function () {
    this.set('surveysPreview', Ember.A([]));
  },

  removeSingleDevice: function (event) {
    var id, devicesPreview, i;
    id = event.context.get('clientId');
    devicesPreview = this.get('devicesPreview');
    for (i = 0; i < devicesPreview.length; i++) {
      if (devicesPreview.objectAt(i).clientId == id) {
        devicesPreview.removeAt(i);
      }
    }
    this.set('devicesPreview', devicesPreview);
  },

  removeAllDevices: function () {
    this.set('devicesPreview', Ember.A([]));
  }
});

});

loader.register('akvo-flow/views/devices/assignments-list-tab-view', function(require) {
FLOW.AssignmentsListTabView = FLOW.View.extend({

  editSurveyAssignment: function (event) {
    FLOW.selectedControl.set('selectedSurveyAssignment', event.context);
    FLOW.router.transitionTo('navDevices.editSurveysAssignment');
  },

  createNewAssignment: function () {
    var newAssignment;
    newAssignment = FLOW.store.createRecord(FLOW.SurveyAssignment, {});
    FLOW.selectedControl.set('selectedSurveyAssignment', newAssignment);
    FLOW.router.transitionTo('navDevices.editSurveysAssignment');
  }
});

FLOW.AssignmentView = FLOW.View.extend({
  tagName: 'span',
  deleteSurveyAssignment: function () {
    var assignment;
    assignment = FLOW.store.find(FLOW.SurveyAssignment, this.content.get('keyId'));
    if (assignment !== null) {
      assignment.deleteRecord();
      FLOW.store.commit();
    }
  }
});

});

loader.register('akvo-flow/views/devices/devices-views', function(require) {
FLOW.CurrentDevicesTabView = Ember.View.extend({
  // FLOW.CurrentDevicesTabView = FLOW.View.extend({
  showDeleteDevicesDialogBool: false,
  showAddToGroupDialogBool: false,
  showRemoveFromGroupDialogBool: false,
  showManageDeviceGroupsDialogBool: false,
  newDeviceGroupName: null,
  // bound to devices-list.handlebars
  changedDeviceGroupName: null,
  selectedDeviceGroup: null,
  selectedDeviceGroupForDelete: null,

  // bound to devices-list.handlebars
  showAddToGroupDialog: function () {
    this.set('selectedDeviceGroup', null);
    this.set('showAddToGroupDialogBool', true);
  },

  showRemoveFromGroupDialog: function () {
    this.set('showRemoveFromGroupDialogBool', true);
  },

  cancelAddToGroup: function () {
    this.set('showAddToGroupDialogBool', false);
  },

  showManageDeviceGroupsDialog: function () {
    this.set('newDeviceGroupName', null);
    this.set('changedDeviceGroupName', null);
    this.set('selectedDeviceGroup', null);
    this.set('showManageDeviceGroupsDialogBool', true);
  },

  cancelManageDeviceGroups: function () {
    this.set('showManageDeviceGroupsDialogBool', false);
  },

  doAddToGroup: function () {
    if (this.get('selectedDeviceGroup') !== null) {
      var selectedDeviceGroupId = this.selectedDeviceGroup.get('keyId');
      var selectedDeviceGroupName = this.selectedDeviceGroup.get('code');
      var selectedDevices = FLOW.store.filter(FLOW.Device, function (data) {
        if (data.get('isSelected') === true) {
          return true;
        } else {
          return false;
        }
      });
      selectedDevices.forEach(function (item) {
        item.set('deviceGroupName', selectedDeviceGroupName);
        item.set('deviceGroup', selectedDeviceGroupId);
      });
    }
    FLOW.store.commit();
    this.set('showAddToGroupDialogBool', false);
  },

  // TODO repopulate list after update
  doRemoveFromGroup: function () {
    var selectedDevices = FLOW.store.filter(FLOW.Device, function (data) {
      if (data.get('isSelected') === true) {
        return true;
      } else {
        return false;
      }
    });
    selectedDevices.forEach(function (item) {
      item.set('deviceGroupName', null);
      item.set('deviceGroup', null);
    });

    FLOW.store.commit();
    this.set('showRemoveFromGroupDialogBool', false);
  },

  cancelRemoveFromGroup: function () {
    this.set('showRemoveFromGroupDialogBool', false);
  },

  copyDeviceGroupName: function () {
    if (this.get('selectedDeviceGroup') !== null) {
      this.set('changedDeviceGroupName', this.selectedDeviceGroup.get('code'));
    }
  }.observes('this.selectedDeviceGroup'),

  // TODO update device group name in tabel.
  doManageDeviceGroups: function () {
    var allDevices;
    if (this.get('selectedDeviceGroup') !== null) {
      var selectedDeviceGroupId = this.selectedDeviceGroup.get('keyId');

      // this could have been changed in the UI
      var originalSelectedDeviceGroup = FLOW.store.find(FLOW.DeviceGroup, selectedDeviceGroupId);

      if (originalSelectedDeviceGroup.get('code') != this.get('changedDeviceGroupName')) {
        var newName = this.get('changedDeviceGroupName');
        originalSelectedDeviceGroup.set('code', newName);

        allDevices = FLOW.store.filter(FLOW.Device, function (data) {
          return true;
        });
        allDevices.forEach(function (item) {
          if (parseInt(item.get('deviceGroup'), 10) == selectedDeviceGroupId) {
            item.set('deviceGroupName', newName);
          }
        });
      }
    } else if (this.get('newDeviceGroupName') !== null) {
      FLOW.store.createRecord(FLOW.DeviceGroup, {
        "code": this.get('newDeviceGroupName')
      });
    }

    this.set('selectedDeviceGroup', null);
    this.set('newDeviceGroupName', null);
    this.set('changedDeviceGroupName', null);

    FLOW.store.commit();
    this.set('showManageDeviceGroupsDialogBool', false);
  },

  deleteDeviceGroup: function () {
    var dgroup, devicesInGroup;
    dgroup = this.get('selectedDeviceGroupForDelete');
    if (dgroup !== null) {

      devicesInGroup = FLOW.store.filter(FLOW.Device, function (item) {
        return item.get('deviceGroup') == dgroup.get('keyId');
      });
      devicesInGroup.forEach(function (item) {
        item.set('deviceGroupName', null);
        item.set('deviceGroup', null);
      });

      FLOW.store.commit();

      dgroup.deleteRecord();
      FLOW.store.commit();
    }
    this.set('showManageDeviceGroupsDialogBool', false);
  }
});


// TODO not used?
FLOW.SavingDeviceGroupView = FLOW.View.extend({
  showDGSavingDialogBool: false,

  showDGSavingDialog: function () {
    if (FLOW.DeviceGroupControl.get('allRecordsSaved')) {
      this.set('showDGSavingDialogBool', false);
    } else {
      this.set('showDGSavingDialogBool', true);
    }
  }.observes('FLOW.deviceGroupControl.allRecordsSaved')
});

});

loader.register('akvo-flow/views/devices/survey-bootstrap-view', function(require) {
// I18N
// Ember.String.loc('_request_submitted_email_will_be_sent');

FLOW.SurveyBootstrap = FLOW.View.extend({
  surveysPreview: Ember.A([]),
  includeDBInstructions: false,
  dbInstructions: '',
  notificationEmail: '',

  init: function () {
    this._super();
    FLOW.selectedControl.set('selectedSurveys', null);
  },

  selectAllSurveys: function () {
	var selected = FLOW.surveyControl.get('content').filter(function (item) {
	  return item.get('status') === "PUBLISHED";
    });
	FLOW.selectedControl.set('selectedSurveys', selected);
  },

  deselectAllSurveys: function () {
    FLOW.selectedControl.set('selectedSurveys', []);
  },

  addSelectedSurveys: function () {
    var sgName = FLOW.selectedControl.selectedSurveyGroup.get('code');

    FLOW.selectedControl.get('selectedSurveys').forEach(function (item) {
      item.set('surveyGroupName', sgName);
    });

    this.surveysPreview.pushObjects(FLOW.selectedControl.get('selectedSurveys'));
    // delete duplicates
    this.set('surveysPreview', FLOW.ArrNoDupe(this.get('surveysPreview')));
  },

  removeSingleSurvey: function (event) {
    var id, surveysPreview, i;
    id = event.context.get('clientId');
    surveysPreview = this.get('surveysPreview');
    for (i = 0; i < surveysPreview.length; i++) {
      if (surveysPreview.objectAt(i).clientId == id) {
        surveysPreview.removeAt(i);
      }
    }
    this.set('surveysPreview', surveysPreview);
  },

  removeAllSurveys: function () {
    this.set('surveysPreview', Ember.A([]));
  },

  sendSurveys: function () {
    var surveyIds, payload;

    if (this.get('surveysPreview').length === 0 && !this.get('includeDBInstructions')) {
      this.showMessage(Ember.String.loc('_survey_or_db_instructions_required'));
      return;
    }

    if (this.get('includeDBInstructions') && this.get('dbInstructions') === '') {
      this.showMessage(Ember.String.loc('_missing_db_instructions'));
      return;
    }

    if (!this.get('notificationEmail')) {
      this.showMessage(Ember.String.loc('_notification_email_required'));
      return;
    }

    payload = {
      action: 'generateBootstrapFile',
      email: this.get('notificationEmail')
    };

    surveyIds = [];

    this.get('surveysPreview').forEach(function (item) {
      surveyIds.push(item.get('keyId'));
    });

    payload.surveyIds = surveyIds;

    if (this.get('includeDBInstructions')) {
      payload.dbInstructions = this.get('dbInstructions');
    }

    FLOW.store.findQuery(FLOW.Action, payload);

    this.reset();
  },

  showMessage: function (msg) {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_manual_survey_transfer'));
    FLOW.dialogControl.set('message', msg);
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  reset: function () {
    this.removeAllSurveys();
    this.deselectAllSurveys();
    this.set('dbInstructions', '');
    this.set('includeDBInstructions', false);
    this.set('notificationEmail', '');
  }
});

});

loader.register('akvo-flow/views/maps/map-views-common-public', function(require) {
FLOW.NavMapsView = FLOW.View.extend({
  templateName: 'navMaps/nav-maps-common',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,
  map: null,
  geoModel: null,
  allowFilters: false,

  init: function () {
    this._super();
    this.detailsPaneElements = "#pointDetails h2" +
      ", #pointDetails dl" +
      ", #pointDetails img" +
      ", #pointDetails .imgContainer" +
      ", .placeMarkBasicInfo" +
      ", .noDetails";
    this.detailsPaneVisible = false;
  },

  redoMap: function() {
      var n, e, s, w, mapBounds;
      mapBounds = this.map.getBounds();
      // get current bounding box of the visible map
      n = mapBounds.getNorthEast().lat;
      e = mapBounds.getNorthEast().lng;
      s = mapBounds.getSouthWest().lat;
      w = mapBounds.getSouthWest().lng;

      // bound east and west
      e = (e + 3 * 180.0) % (2 * 180.0) - 180.0;
      w = (w + 3 * 180.0) % (2 * 180.0) - 180.0;

      // create bounding box object
      var bb = this.geoModel.create_bounding_box(n, e, s, w);

      // create the best set of geocell box cells which covers
      // the current viewport
      var bestBB = this.geoModel.best_bbox_search_cells(bb);

      // adapt the points shown on the map
      FLOW.router.mapsController.adaptMap(bestBB, this.map.getZoom());
    },

  /**
    Once the view is in the DOM create the map
  */
  didInsertElement: function () {

    var self = this;

    if(FLOW.Env.mapsProvider === 'google'){
      this.map = new L.Map('flowMap', {center: new L.LatLng(-0.703107, 36.765), zoom: 2});
      var roadmap = new L.Google("ROADMAP");
      var terrain = new L.Google('TERRAIN');
      var satellite = new L.Google('SATELLITE');
      this.map.addLayer(roadmap);
      this.map.addControl(new L.Control.Layers({
        'Roadmap': roadmap,
        'Satellite': satellite,
        'Terrain': terrain
      }, {}));
    } else {
      // insert the map
      var options = {
          minZoom: 2,
          maxZoom: 18
      };
      this.map = L.mapbox.map('flowMap', 'akvo.he30g8mm', options).setView([-0.703107, 36.765], 2);

      L.control.layers({
        'Terrain': L.mapbox.tileLayer('akvo.he30g8mm').addTo(this.map),
        'Streets': L.mapbox.tileLayer('akvo.he2pdjhk'),
        'Satellite': L.mapbox.tileLayer('akvo.he30neh4')
      }).addTo(this.map);
    }

    // add scale indication to map
    L.control.scale({position:'topleft', maxWidth:150}).addTo(this.map);

    // couple listener to end of zoom or drag
    this.map.on('moveend', function (e) {
      self.redoMap();
    });

    FLOW.router.mapsController.set('map', this.map);
    this.geoModel = create_geomodel();

    //load points for the visible map
    this.redoMap();

    this.$('#mapDetailsHideShow').click(function () {
      self.handleShowHideDetails();
    });

    // Slide in detailspane after 1 sec
    this.hideDetailsPane(1000);
  },

  /**
    Helper function to dispatch to either hide or show details pane
  */
  handleShowHideDetails: function () {
    if (this.detailsPaneVisible) {
      this.hideDetailsPane();
    } else {
      this.showDetailsPane();
    }
  },

  /**
    Slide in the details pane
  */
  showDetailsPane: function () {
    var button;

    button = this.$('#mapDetailsHideShow');
    button.html('Hide &rsaquo;');
    this.set('detailsPaneVisible', true);

    this.$('#flowMap').animate({
      width: '75%'
    }, 200);
    this.$('#pointDetails').animate({
      width: '24.5%'
    }, 200).css({
      overflow: 'auto',
      marginLeft: '-2px'
    });
    this.$(this.detailsPaneElements, '#pointDetails').animate({
      opacity: '1'
    }, 200).css({
      display: 'inherit'
    });
  },


  /**
    Slide out details pane
  */
  hideDetailsPane: function (delay) {
    var button;

    delay = typeof delay !== 'undefined' ? delay : 0;
    button = this.$('#mapDetailsHideShow');

    this.set('detailsPaneVisible', false);
    button.html('');

    this.$('#flowMap').delay(delay).animate({
      width: '99.25%'
    }, 200);
    this.$('#pointDetails').delay(delay).animate({
      width: '0.25%'
    }, 200).css({
      overflow: 'scroll-y',
      marginLeft: '-2px'
    });
    this.$(this.detailsPaneElements, '#pointDetails').delay(delay).animate({
      opacity: '0',
      display: 'none'
    });
  },


  /**
    If a placemark is selected and the details pane is hidden make sure to
    slide out
  */
  handlePlacemarkDetails: function () {
    var details;

    details = FLOW.placemarkDetailController.get('content');

    if (!this.detailsPaneVisible) {
      this.showDetailsPane();
    }
    if (!Ember.empty(details) && details.get('isLoaded')) {
      this.populateDetailsPane(details);
    }
  }.observes('FLOW.placemarkDetailController.content.isLoaded'),


  /**
    Populates the details pane with data from a placemark
  */
  populateDetailsPane: function (details) {
    var rawImagePath, verticalBars;

    this.set('showDetailsBool', true);
    details.forEach(function (item) {
      rawImagePath = item.get('stringValue') || '';
      verticalBars = rawImagePath.split('|');
      if (verticalBars.length === 4) {
        FLOW.placemarkDetailController.set('selectedPointCode',
          verticalBars[3]);
      }
    }, this);
  }

});


FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailView = Ember.View.extend({});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});

});

loader.register('akvo-flow/views/maps/map-views-common', function(require) {
FLOW.NavMapsView = FLOW.View.extend({
  templateName: 'navMaps/nav-maps-common',
  showDetailsBool: false,
  detailsPaneElements: null,
  detailsPaneVisible: null,
  map: null,
  marker: null,
  polygons: [],
  mapZoomLevel: 0,
  mapCenter: null,
  mediaMarkers: {},
  selectedMediaMarker: {},
  mediaMarkerSelected: {},
  geoModel: null,
  selectedSurvey: null,
  allowFilters: FLOW.Env.mapsProvider && FLOW.Env.mapsProvider === 'cartodb',

  init: function () {
    this._super();
    this.detailsPaneElements = "#pointDetails h2" +
      ", #pointDetails dl" +
      ", #pointDetails img" +
      ", #pointDetails .imgContainer" +
      ", .placeMarkBasicInfo" +
      ", .noDetails";
  },

  redoMap: function() {
      var n, e, s, w, mapBounds;
      mapBounds = this.map.getBounds();
      // get current bounding box of the visible map
      n = mapBounds.getNorthEast().lat;
      e = mapBounds.getNorthEast().lng;
      s = mapBounds.getSouthWest().lat;
      w = mapBounds.getSouthWest().lng;

      // bound east and west
      e = (e + 3 * 180.0) % (2 * 180.0) - 180.0;
      w = (w + 3 * 180.0) % (2 * 180.0) - 180.0;

      // create bounding box object
      var bb = this.geoModel.create_bounding_box(n, e, s, w);

      // create the best set of geocell box cells which covers
      // the current viewport
      var bestBB = this.geoModel.best_bbox_search_cells(bb);

      // adapt the points shown on the map
      FLOW.router.mapsController.adaptMap(bestBB, this.map.getZoom());
    },

  /**
    Once the view is in the DOM create the map
  */
  didInsertElement: function () {
    var self = this;

    if (FLOW.Env.mapsProvider === 'cartodb') {
      self.insertCartodbMap();
    } else {
      if (FLOW.Env.mapsProvider === 'google') {
        self.insertGoogleMap();
      } else {
        self.insertMapboxMap();
      }
      // couple listener to end of zoom or drag
      this.map.on('moveend', function (e) {
        self.redoMap();
      });
      FLOW.router.mapsController.set('map', this.map);
      this.geoModel = create_geomodel();
      //load points for the visible map
      this.redoMap();
    }

    // add scale indication to map
    L.control.scale({position:'topleft', maxWidth:150}).addTo(this.map);

    this.$('#mapDetailsHideShow').click(function () {
      self.toggleProperty('detailsPaneVisible');
    });

    self.set('detailsPaneVisible', false);

    self.detailsPanelListeners();
  },

  insertGoogleMap: function () {
    this.map = new L.Map('flowMap', {center: new L.LatLng(-0.703107, 36.765), zoom: 2});
    var roadmap = new L.Google("ROADMAP");
    var terrain = new L.Google('TERRAIN');
    var satellite = new L.Google('SATELLITE');
    this.map.addLayer(roadmap);
    this.map.addControl(new L.Control.Layers({
      'Roadmap': roadmap,
      'Satellite': satellite,
      'Terrain': terrain
    }, {}));
  },

  insertMapboxMap: function() {
      var options = {
          minZoom: 2,
          maxZoom: 18
      };
    this.map = L.mapbox.map('flowMap', 'akvo.he30g8mm', options).setView([-0.703107, 36.765], 2);
    L.control.layers({
      'Terrain': L.mapbox.tileLayer('akvo.he30g8mm').addTo(this.map),
      'Streets': L.mapbox.tileLayer('akvo.he2pdjhk'),
      'Satellite': L.mapbox.tileLayer('akvo.he30neh4')
    }).addTo(this.map);
  },

  insertCartodbMap: function() {
    var self = this;

    $.ajaxSetup({
    	beforeSend: function(){
    		FLOW.savingMessageControl.numLoadingChange(1);
        },
    	complete: function(){
    		FLOW.savingMessageControl.numLoadingChange(-1);
        }
    });

    this.map = L.map('flowMap', {scrollWheelZoom: true}).setView([26.11598592533351, 1.9335937499999998], 2);

    var bounds = new L.LatLngBounds(this.map.getBounds().getSouthWest(), this.map.getBounds().getNorthEast());

    this.map.options.maxBoundsViscosity = 1.0;
    this.map.options.maxBounds = bounds;
    this.map.options.maxZoom = 18;
    this.map.options.minZoom = 2;

    var hereAttr = 'Map &copy; 1987-2014 <a href="http://developer.here.com">HERE</a>',
			hereUrl = 'https://{s}.{base}.maps.cit.api.here.com/maptile/2.1/maptile/{mapID}/{scheme}/{z}/{x}/{y}/256/{format}?app_id={app_id}&app_code={app_code}',
      mbAttr = 'Map &copy; <a href="http://openstreetmap.org">OSM</a>',
      mbUrl = 'http://{s}.tiles.mapbox.com/v3/akvo.he30g8mm/{z}/{x}/{y}.png';

    var normal = L.tileLayer(hereUrl, {
      scheme: 'normal.day.transit',
      format: 'png8',
      attribution: hereAttr,
      subdomains: '1234',
      mapID: 'newest',
      app_id: FLOW.Env.hereMapsAppId,
      app_code: FLOW.Env.hereMapsAppCode,
      base: 'base'
    }).addTo(this.map),
    terrain  = L.tileLayer(mbUrl, {
      attribution: mbAttr,
      subdomains: 'abc'
    }),
    satellite  = L.tileLayer(hereUrl, {
      scheme: 'hybrid.day',
      format: 'jpg',
      attribution: hereAttr,
      subdomains: '1234',
      mapID: 'newest',
      app_id: FLOW.Env.hereMapsAppId,
      app_code: FLOW.Env.hereMapsAppCode,
      base: 'aerial'
    });

    var baseLayers = {
			"Normal": normal,
            "Terrain": terrain,
			"Satellite": satellite
		};

    FLOW.addExtraMapBoxTileLayer(baseLayers);

    L.control.layers(baseLayers).addTo(this.map);

    FLOW.router.mapsController.set('map', this.map);

    this.map.on('click', function(e) {
      self.clearMap(); //remove any previously loaded point data
    });

    this.map.on('zoomend', function() {
      $('body, html, #flowMap').scrollTop(0);
    });
  },

  detailsPanelListeners: function(){
      var self = this;
      $(document.body).on('click', '.project-geoshape', function(){
        if(self.polygons.length > 0){
          $(this).html(Ember.String.loc('_project_onto_main_map'));
          for(var i=0; i<self.polygons.length; i++){
            self.map.removeLayer(self.polygons[i]);
          }
          //restore the previous zoom level and map center
          self.map.setZoom(self.mapZoomLevel);
          self.map.panTo(self.mapCenter);
          self.polygons = [];
        }else{
          $(this).html(Ember.String.loc('_clear_geoshape_from_main_map'));
          self.projectGeoshape($(this).data('geoshape-object'));
        }
      });

      $(document.body).on('mouseover', '.media', function(){
        var mediaObject = $(this).data('coordinates');
        var mediaMarkerIcon = new L.Icon({
          iconUrl: 'images/media-marker.png',
          iconSize: [11, 11]
        }), selectedMediaMarkerIcon = new L.Icon({
          iconUrl: 'images/media-marker-selected.png',
          iconSize: [11, 11]
        });
        if(mediaObject !== '') {
          var filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf("/")+1).split(".")[0];
          var mediaCoordinates = [mediaObject['location']['latitude'], mediaObject['location']['longitude']];
          if(!(filename in self.mediaMarkers)) {
            self.mediaMarkers[filename] = new L.marker(mediaCoordinates, {icon: mediaMarkerIcon}).addTo(self.map);
          } else {
            self.selectedMediaMarker[filename] = new L.marker(mediaCoordinates, {icon: selectedMediaMarkerIcon}).addTo(self.map);
          }
        }
      });

      $(document.body).on('mouseout', '.media', function(){
        var mediaObject = $(this).data('coordinates');
        if(mediaObject !== '') {
          var filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf("/")+1).split(".")[0];
          if(filename in self.mediaMarkers && !(filename in self.mediaMarkerSelected)) {
            self.map.removeLayer(self.mediaMarkers[filename]);
            delete self.mediaMarkers[filename];
          } else {
            self.map.removeLayer(self.selectedMediaMarker[filename]);
            delete self.selectedMediaMarker[filename];
          }
        }
      });

      $(document.body).on('click', '.media-location', function(){
        var mediaObject = $(this).data('coordinates');
        var mediaMarkerIcon = new L.Icon({
          iconUrl: 'images/media-marker.png',
          iconSize: [11, 11]
        });
        if(mediaObject !== '') {
          var filename = mediaObject.filename.substring(mediaObject.filename.lastIndexOf("/")+1).split(".")[0];
          var mediaCoordinates = [mediaObject['location']['latitude'], mediaObject['location']['longitude']];
          if(!(filename in self.mediaMarkerSelected)) {
            $(this).html(Ember.String.loc('_hide_photo_on_map'));
            self.mediaMarkers[filename] = new L.marker(mediaCoordinates, {icon: mediaMarkerIcon}).addTo(self.map);
            self.mediaMarkerSelected[filename] = true;
          } else {
            $(this).html(Ember.String.loc('_show_photo_on_map'));
            self.map.removeLayer(self.mediaMarkers[filename]);
            delete self.mediaMarkers[filename];
            delete self.mediaMarkerSelected[filename];
          }
        }
      });
  },

  surveySelection: function () {
      this.clearMap();
      FLOW.router.mapsController.clearSurveyDataLayer();
      if (!Ember.none(this.get('selectedSurvey'))) {
          FLOW.router.mapsController.loadNamedMap(this.selectedSurvey.get('keyId'));
      }
  }.observes('this.selectedSurvey'),

  surveyGroupSelection: function () {
      this.clearMap();
      FLOW.router.mapsController.clearSurveyDataLayer();
  }.observes('FLOW.selectedControl.selectedSurveyGroup'),

  /**
    If a placemark is selected and the details pane is hidden make sure to
    slide out
  */
  handlePlacemarkDetails: function () {
    var details = FLOW.placemarkDetailController.get('content');

    this.showDetailsPane();
    if (!Ember.empty(details) && details.get('isLoaded')) {
      this.populateDetailsPane(details);
    }
  }.observes('FLOW.placemarkDetailController.content.isLoaded'),

  /**
    Populates the details pane with data from a placemark
  */
  populateDetailsPane: function (details) {
    var rawImagePath, verticalBars;

    this.set('showDetailsBool', true);
    details.forEach(function (item) {
      rawImagePath = item.get('stringValue') || '';
      verticalBars = rawImagePath.split('|');
      if (verticalBars.length === 4) {
        FLOW.placemarkDetailController.set('selectedPointCode',
          verticalBars[3]);
      }
    }, this);
  },

  //function to project geoshape from details panel to main map canvas
  projectGeoshape: function(geoShapeObject){
    var points = [], geoShape;

    //before fitting the geoshape to map, get the current
    //zoom level and map center first and save them
    this.mapZoomLevel = this.map.getZoom();
    this.mapCenter = this.map.getCenter();

    var geoshapeCoordinatesArray, geoShapeObjectType = geoShapeObject['features'][0]['geometry']['type'];
    if(geoShapeObjectType === "Polygon"){
      geoshapeCoordinatesArray = geoShapeObject['features'][0]['geometry']['coordinates'][0];
    } else {
      geoshapeCoordinatesArray = geoShapeObject['features'][0]['geometry']['coordinates'];
    }

    for(var j=0; j<geoshapeCoordinatesArray.length; j++){
      points.push([geoshapeCoordinatesArray[j][1], geoshapeCoordinatesArray[j][0]]);
    }

    if(geoShapeObjectType === "Polygon"){
      geoShape = L.polygon(points).addTo(this.map);
    }else if (geoShapeObjectType === "MultiPoint") {
      var geoShapeMarkersArray = [];
      for (var i = 0; i < points.length; i++) {
        geoShapeMarkersArray.push(L.marker([points[i][0],points[i][1]]));
      }
      geoShape = L.featureGroup(geoShapeMarkersArray).addTo(this.map);
    }else if (geoShapeObjectType === "LineString") {
      geoShape = L.polyline(points).addTo(this.map);
    }
    this.map.fitBounds(geoShape.getBounds());
    this.polygons.push(geoShape);
  },

  clearMap: function() {
    var self = this;
    self.set('detailsPaneVisible', false);
    if (self.marker) {
      self.map.removeLayer(self.marker);
    }

    if (!Ember.empty(self.mediaMarkers)) {
      for (mediaMarker in self.mediaMarkers) {
        self.map.removeLayer(self.mediaMarkers[mediaMarker]);
      }
    }

    if (self.polygons.length > 0) {
      for (var i=0; i<self.polygons.length; i++) {
        self.map.removeLayer(self.polygons[i])
      }
      //restore the previous zoom level and map center
      self.map.setView(self.mapCenter, self.mapZoomLevel);
      self.polygons = [];
    }
  },

  /*Place a marker to highlight clicked point of layer on cartodb map*/
  placeMarker: function(latlng){
      //if there's a previously loaded marker, first remove it
      if (this.marker) {
          this.map.removeLayer(this.marker);
      }

      var markerIcon = new L.Icon({
          iconUrl: 'images/marker.svg',
          iconSize: [10, 10]
      });
      this.marker = new L.marker(FLOW.router.mapsController.get('markerCoordinates'), {icon: markerIcon});
      this.map.addLayer(this.marker);

      this.showDetailsPane();
  }.observes('FLOW.router.mapsController.markerCoordinates'),

  detailsPaneShowHide: function(){
      var button = this.$('#mapDetailsHideShow');
      var display = this.detailsPaneVisible;

      button.html('&lsaquo; ' + Ember.String.loc((display) ? '_hide' : '_show'));

      this.$('#flowMap').animate({
        width: (display) ? '75%' : '99.25%'
      }, 200);
      this.$('#pointDetails').animate({
        width: (display) ? '24.5%' : '0.25%'
      }, 200).css({
        overflow: (display) ? 'auto' : 'scroll-y',
        marginLeft: '-2px'
      });
      this.$(this.detailsPaneElements, '#pointDetails').animate({
        opacity: (display) ? '1' : '0',
        display: (display) ? 'inherit' : 'none'
      });
  }.observes('this.detailsPaneVisible'),

  showDetailsPane: function(){
      if (!this.detailsPaneVisible) {
        this.set('detailsPaneVisible', true);
      }
  }
});

FLOW.countryView = FLOW.View.extend({});
FLOW.PlacemarkDetailView = Ember.View.extend({
    cartoMaps: FLOW.Env.mapsProvider && FLOW.Env.mapsProvider === 'cartodb'
});
FLOW.PlacemarkDetailPhotoView = Ember.View.extend({});

FLOW.GeoshapeMapView = FLOW.View.extend({
  templateName: 'navMaps/geoshape-map',
  geoshape: null,

  didInsertElement: function() {
    this.set('geoshape', JSON.parse(this.get('parentView.geoShapeObject')));
    if (this.get('isPolygon') || this.get('isLineString') || this.get('isMultiPoint')) {
      var containerNode = this.get('element').getElementsByClassName('geoshapeMapContainer')[0];
      containerNode.innerHTML = "";
      if (containerNode) {
        FLOW.drawGeoShape(containerNode, this.get('geoshape'));
      }
    }
  },

  length: function() {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.length
  }.property('this.geoshape'),

  area: function() {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.area
  }.property('this.geoshape'),

  pointCount: function() {
    return this.geoshape === null ? null : this.geoshape.features[0].properties.pointCount
  }.property('this.geoshape'),

  isPolygon: function() {
    var geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    } else {
      return geoshape['features'].length > 0 &&
        geoshape['features'][0]["geometry"]["type"] === "Polygon"
    }
  }.property('this.geoshape'),

  isLineString: function() {
    var geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    } else {
      return geoshape['features'].length > 0 &&
        geoshape['features'][0]["geometry"]["type"] === "LineString"
    }
  }.property('this.geoshape'),

  isMultiPoint: function() {
    var geoshape = this.get('geoshape');
    if (geoshape == null) {
      return false;
    } else {
      return geoshape['features'].length > 0 &&
        geoshape['features'][0]["geometry"]["type"] === "MultiPoint"
    }
  }.property('this.geoshape'),

  geoshapeString: function() {
    return this.geoshape === null ? null : JSON.stringify(this.geoshape);
  }.property('this.geoshape')
});

});

loader.register('akvo-flow/views/messages/message-view', function(require) {
FLOW.MessagesListView = FLOW.View.extend({

  doInstanceQuery: function () {
    this.set('since', FLOW.metaControl.get('since'));
    FLOW.messageControl.doInstanceQuery(this.get('since'));
  },

  doNextPage: function () {
    FLOW.messageControl.get('sinceArray').pushObject(FLOW.metaControl.get('since'));
    this.doInstanceQuery();
  },

  doPrevPage: function () {
    FLOW.messageControl.get('sinceArray').popObject();
    FLOW.metaControl.set('since', FLOW.messageControl.get('sinceArray')[FLOW.messageControl.get('sinceArray').length - 1]);
    this.doInstanceQuery();
  },

  // If the number of items in the previous call was 20 (a full page) we assume that there are more.
  // This is not foolproof, but will only lead to an empty next page in 1/20 of the cases
  hasNextPage: function () {
    if (FLOW.metaControl.get('num') == 20) {
      return true;
    } else {
      return false;
    }
  }.property('FLOW.metaControl.num'),

  // not perfect yet, sometimes previous link is shown while there are no previous pages.
  hasPrevPage: function () {
    if (FLOW.messageControl.get('sinceArray').length === 1) {
      return false;
    } else {
      return true;
    }
  }.property('FLOW.messageControl.sinceArray.length')

});

});

loader.register('akvo-flow/views/reports/export-reports-views', function(require) {
/*global Ember, $, FLOW */

FLOW.ReportLoader = Ember.Object.create({
  criteria: null,
  timeout: 30000,
  requestInterval: 3000,

  payloads: {
	DATA_CLEANING: {
	  surveyId: '75201',
	  exportType: 'DATA_CLEANING',
	  opts: {
		exportMode: 'DATA_CLEANING',
		lastCollection: 'false',
	  }
	},
	DATA_ANALYSIS: {
	  surveyId: '75201',
	  exportType: 'DATA_ANALYSIS',
	  opts: {
		exportMode: 'DATA_ANALYSIS',
	  }
	},
    COMPREHENSIVE: {
      surveyId: '75201',
      exportType: 'COMPREHENSIVE',
      opts: {
        exportMode: 'COMPREHENSIVE',
      }
    },
    GEOSHAPE: {
      surveyId: '75201',
      exportType: 'GEOSHAPE',
      opts: {
        questionId: '12345'
      }
    },
    SURVEY_FORM: {
      surveyId: '75201',
      exportType: 'SURVEY_FORM',
      opts: {}
    }
  },

  load: function (exportType, surveyId, opts) {
    var criteria;

    if (this.get('criteria')) {
      return;
    }

    Ember.assert('exportType param is required', exportType !== undefined);
    Ember.assert('surveyId param is required', surveyId !== undefined);

    criteria = Ember.copy(this.get('payloads')[exportType]);
    criteria.surveyId = '' + surveyId;
    criteria.baseURL = location.protocol + '//' + location.host;

    criteria.opts.imgPrefix = FLOW.Env.photo_url_root;
    criteria.opts.uploadUrl = FLOW.Env.surveyuploadurl;
    criteria.opts.appId = FLOW.Env.appId;

    if (opts) {
      Ember.keys(opts).forEach(function (k) {
        criteria.opts[k] = opts[k];
      });
    }

    criteria.opts.lastCollection = '' + (exportType === 'DATA_CLEANING' && FLOW.selectedControl.get('selectedSurveyGroup').get('monitoringGroup') && !!FLOW.editControl.lastCollection);

    var fromDate = FLOW.dateControl.get('fromDate');
    if (fromDate == null) {
      delete criteria.opts.from;
    } else {
      criteria.opts.from = fromDate;
    }
    var toDate = FLOW.dateControl.get('toDate');
    if (toDate == null) {
      delete criteria.opts.to;
    } else {
      criteria.opts.to = toDate;
    }
    criteria.opts.email = FLOW.currentUser.email;
    criteria.opts.flowServices = FLOW.Env.flowServices;

    this.set('criteria', criteria);
    FLOW.savingMessageControl.numLoadingChange(1);
    this.requestReport();
  },

  requestReport: function () {
	this.set('processing', true);
	$.ajax({
	  url: FLOW.Env.flowServices + '/generate',
	  data: {
		criteria: JSON.stringify(this.get('criteria'))
	  },
	  jsonpCallback: 'FLOW.ReportLoader.handleResponse',
	  dataType: 'jsonp',
	  timeout: this.timeout
	});

	Ember.run.later(this, this.handleError, this.timeout);
  },

  handleResponse: function (resp) {
    if (!resp || resp.status !== 'OK') {
      FLOW.savingMessageControl.numLoadingChange(-1);
      this.showError();
      return;
    }
    if (resp.message === 'PROCESSING') {
      this.set('processing', false);
      this.showEmailNotification();
    } else if (resp.file && this.get('processing')) {
      FLOW.savingMessageControl.numLoadingChange(-1);
      this.set('processing', false);
      this.set('criteria', null);
      $('#downloader').attr('src', FLOW.Env.flowServices + '/report/' + resp.file);
    }
  },

  handleError: function () {
    if (this.get('processing')) {
      FLOW.savingMessageControl.numLoadingChange(-1);
      this.showError();
    }
  },

  showError: function () {
	  FLOW.savingMessageControl.numLoadingChange(-1);
    this.set('processing', false);
    this.set('criteria', null);
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_error_generating_report'));
    FLOW.dialogControl.set('message', Ember.String.loc('_error_generating_report_try_later'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  showEmailNotification: function () {
    FLOW.savingMessageControl.numLoadingChange(-1);
    this.set('processing', false);
    this.set('criteria', null);
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_your_report_is_being_prepared'));
    FLOW.dialogControl.set('message', Ember.String.loc('_we_will_notify_via_email'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  }
});

FLOW.ExportReportsAppletView = FLOW.View.extend({
  showRawDataReportApplet: false,
  showComprehensiveReportApplet: false,
  showGoogleEarthFileApplet: false,
  showSurveyFormApplet: false,
  showComprehensiveDialog: false,
  showRawDataImportApplet: false,
  showGoogleEarthButton: false,

  didInsertElement: function () {
    FLOW.selectedControl.set('surveySelection', FLOW.SurveySelection.create());
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.editControl.set('useQuestionId', false);
    FLOW.dateControl.set('fromDate', null);
    FLOW.dateControl.set('toDate', null);
    FLOW.uploader.registerEvents();
  },

  selectedSurvey: function () {
    if (!Ember.none(FLOW.selectedControl.get('selectedSurvey')) && !Ember.none(FLOW.selectedControl.selectedSurvey.get('keyId'))){
      return FLOW.selectedControl.selectedSurvey.get('keyId');
    } else {
      return null;
    }
  }.property('FLOW.selectedControl.selectedSurvey'),

  selectedQuestion: function () {
    if (!Ember.none(FLOW.selectedControl.get('selectedQuestion'))
        && !Ember.none(FLOW.selectedControl.selectedQuestion.get('keyId'))){
      return FLOW.selectedControl.selectedQuestion.get('keyId');
    } else {
      return null;
    }
  }.property('FLOW.selectedControl.selectedQuestion'),

  showLastCollection: function () {
    return FLOW.Env.showMonitoringFeature && FLOW.selectedControl.selectedSurveyGroup && FLOW.selectedControl.selectedSurveyGroup.get('monitoringGroup');
  }.property('FLOW.selectedControl.selectedSurveyGroup'),

  showDataCleaningReport: function () {
	var opts = {}, sId = this.get('selectedSurvey');
	FLOW.ReportLoader.load('DATA_CLEANING', sId, opts);
  },

  showDataAnalysisReport: function () {
	var opts = {}, sId = this.get('selectedSurvey');
    FLOW.ReportLoader.load('DATA_ANALYSIS', sId, opts);
  },

  showComprehensiveReport: function () {
    var opts = {}, sId = this.get('selectedSurvey');

    FLOW.ReportLoader.load('COMPREHENSIVE', sId, opts);
  },

  showGeoshapeReport: function () {
    var sId = this.get('selectedSurvey');
    var qId = this.get('selectedQuestion');
    if (!sId || !qId) {
      this.showWarningMessage(
        Ember.String.loc('_export_data'),
        Ember.String.loc('_select_survey_and_geoshape_question_warning')
      );
      return;
    }
    FLOW.ReportLoader.load('GEOSHAPE', sId, {"questionId": qId});
  },

  showSurveyForm: function () {
	var sId = this.get('selectedSurvey');
    if (!sId) {
      this.showWarning();
      return;
    }
    FLOW.ReportLoader.load('SURVEY_FORM', sId);
  },

  importFile: function () {
    var file, sId = this.get('selectedSurvey');
    if (!sId) {
      this.showImportWarning(Ember.String.loc('_import_select_survey'));
      return;
    }

    file = $('#raw-data-import-file')[0];

    if (!file || file.files.length === 0) {
      this.showImportWarning(Ember.String.loc('_import_select_file'));
      return;
    }

    FLOW.uploader.addFile(file.files[0]);
    FLOW.uploader.upload();
  },

  showComprehensiveOptions: function () {
    var sId = this.get('selectedSurvey');
    if (!sId) {
      this.showWarning();
      return;
    }

    FLOW.editControl.set('summaryPerGeoArea', true);
    FLOW.editControl.set('omitCharts', false);
    this.set('showComprehensiveDialog', true);
  },

  showWarning: function () {
    this.showWarningMessage(Ember.String.loc('_export_data'), Ember.String.loc('_applet_select_survey'));
  },

  showImportWarning: function (msg) {
    this.showWarningMessage(Ember.String.loc('_import_clean_data'), msg);
  },

  showWarningMessage: function(header, message) {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', header);
    FLOW.dialogControl.set('message', message);
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  }
});

});

loader.register('akvo-flow/views/reports/report-views', function(require) {
/*global deleteChart, createDoughnutChart, createHBarChart, createVBarChart*/


FLOW.chartView = FLOW.View.extend({
  noChoiceBool: false,
  noDataBool: false,
  chartType: null,
  compactSmaller: true,
  selectedSurvey: null,
  
  downloadOptionQuestions: function () {
	  if (!Ember.none(this.get('selectedSurvey'))) {
		  FLOW.questionControl.downloadOptionQuestions(this.selectedSurvey.get('keyId'));
	  }
  }.observes('this.selectedSurvey'),

  isDoughnut: function () {
    return this.chartType.get('value') == 'doughnut';
  }.property('this.chartType'),

  init: function () {
    this._super();
    this.chartType = FLOW.chartTypeControl.content[0];
  },

  hideChart: function () {
	  return this.get('noChoiceBool') || this.get('noDataBool');
  }.property('noChoiceBool', 'noDataBool'),

  getChartData: function () {
    this.set('noChoiceBool', false);
    if (FLOW.selectedControl.get('selectedQuestion') !== null) {
      FLOW.surveyQuestionSummaryControl.doSurveyQuestionSummaryQuery(FLOW.selectedControl.selectedQuestion.get('keyId'));
      FLOW.chartDataControl.set('questionText', FLOW.selectedControl.selectedQuestion.get('text'));
    } else {
      this.set('noChoiceBool', true);
    }
  },

  buildChart: function () {
    var chartData = [],
      smallerItems = [],
      total = 0,
      max = 0,
      maxPer, i, tot, totPerc;

    deleteChart();

    if (FLOW.surveyQuestionSummaryControl.content.get('isLoaded') === true) {
      FLOW.chartDataControl.set('total', FLOW.surveyQuestionSummaryControl.content.get('length'));
      if (FLOW.chartDataControl.get('total') == 0) {
          this.set('noDataBool', true);
    	  return;
      } else {
    	  this.set('noDataBool', false);
      }

      FLOW.surveyQuestionSummaryControl.get('content').forEach(function (item) {
        total = total + item.get('count');
        if (item.get('count') > max) max = item.get('count');
      });

      // set the maximum of the scale
      maxPer = 100.0 * max / total;

      // if type is doughnut, do doughnut things
      if (this.chartType.get('value') == 'doughnut') {
        i = -1;
        tot = 0;
        totPerc = 0;

        FLOW.surveyQuestionSummaryControl.get('content').forEach(function (item) {
          var percentage = 100.0 * item.get('count') / total,
            percString = percentage.toFixed(1);
          chartData.push({
            "legendLabel": (item.get('response') + ", " + percString + "%") + " (" + item.get('count') + ")" ,
            "percentage": 100.0 * item.get('count') / total
          });
        });

        // sort smallest first
        chartData.sort(function (a, b) {
        	return a.percentage - b.percentage;
        });


        if (this.get('compactSmaller')) {
          chartData.forEach(function (item) {
            if ((totPerc < 5 || item.percentage < 5) && (item.percentage < 7)) {
              totPerc = totPerc + item.percentage;
              i = i + 1;
            }
          });

          tot = 0;

          for (var ii = 0; ii <= i; ii++) {
            smallerItems.push(chartData[ii]);
            tot = tot + chartData[ii].percentage;
          }

          // delete smallest items from chartData
          chartData.splice(0, i + 1);

          // add new item with the size of the smallest items
          chartData.splice(0, 0, {
            "legendLabel": (Ember.String.loc('_smallest_items') + ", " + tot.toFixed(1) + "%"),
            "percentage": tot
          });
        }
        FLOW.chartDataControl.set('chartData', chartData);
        FLOW.chartDataControl.set('smallerItems', smallerItems);
        FLOW.chartDataControl.set('total', total);

        createDoughnutChart();

        // if type vbar, do vbar things
      } else if (this.chartType.get('value') == 'vbar') {
        FLOW.surveyQuestionSummaryControl.get('content').forEach(function (item) {
          chartData.push({
            "legendLabel": (item.get('response')),
            "percentage": 100.0 * item.get('count') / total,
            "itemCount": item.get('count')
          });
        });

        // sort smallest first
        chartData.sort(function (a, b) {
        	return a.percentage - b.percentage;
        });
        FLOW.chartDataControl.set('chartData', chartData);
        FLOW.chartDataControl.set('maxPer', maxPer);
        createVBarChart();

        // if type hbar, do hbar things
      } else if (this.chartType.get('value') == 'hbar') {

        FLOW.surveyQuestionSummaryControl.get('content').forEach(function (item) {
          chartData.push({
            "legendLabel": (item.get('response')),
            "percentage": 100.0 * item.get('count') / total,
            "itemCount": item.get('count')
          });
        });

        // sort smallest first
        chartData.sort(function (a, b) {
        	return a.percentage - b.percentage;
        });
        FLOW.chartDataControl.set('chartData', chartData);
        FLOW.chartDataControl.set('maxPer', maxPer);
        createHBarChart();
      }
    }
  }.observes('FLOW.surveyQuestionSummaryControl.content.isLoaded')
});

});

loader.register('akvo-flow/views/surveys/form-view', function(require) {
FLOW.FormView = Ember.View.extend({
	templateName: 'navSurveys/form',
	showFormBasics: false,

	manageTranslations: false,
	manageNotifications: false,

	form: function() {
		return FLOW.selectedControl.get('selectedSurvey');
	}.property('FLOW.selectedControl.selectedSurvey'),

	toggleShowFormBasics: function () {
		this.set('showFormBasics', !this.get('showFormBasics'));
	},

	isNewForm: function() {
		var form = FLOW.selectedControl.get('selectedSurvey');
		return form && form.get('code') == "New Form";
	}.property('FLOW.selectedControl.selectedSurvey'),

	visibleFormBasics: function() {
		return this.get('isNewForm') || this.get('showFormBasics');
	}.property('showFormBasics'),


	doManageTranslations: function() {
		FLOW.translationControl.populate();
		this.set('manageNotifications', false);
		this.set('manageTranslations', true);
	},

	doManageNotifications: function() {
		FLOW.notificationControl.populate();
		this.set('manageTranslations', false);
		this.set('manageNotifications', true);
	},

	disableFormFields: function () {
		var form = this.get('form');
		return !FLOW.permControl.canEditForm(form);
	}.property('this.form'),

	showFormTranslationsButton: function() {
		var form = this.get('form');
		return FLOW.permControl.canEditForm(form);
	}.property('this.form'),

	showFormDeleteButton: function () {
		var form = this.get('form');
		return FLOW.permControl.canEditForm(form);
	}.property('this.form'),

	showFormPublishButton: function () {
		var form = this.get('form');
		return FLOW.permControl.canEditForm(form);
	}.property('this.form')
});

});

loader.register('akvo-flow/views/surveys/notifications-view', function(require) {
FLOW.NotificationsView = FLOW.View.extend({
  templateName: 'navSurveys/manage-notifications',
  notificationOption: null,
  notificationType: null,
  expiryDate: null,
  notificationDestination: null,
  optionEmpty: false,
  typeEmpty: false,
  destinationEmpty: false,
  dateEmpty: false,

  addNotification: function () {
    var date;

    this.set('optionEmpty', Ember.none(this.get('notificationOption')));
    this.set('typeEmpty', Ember.none(this.get('notificationType')));
    this.set('destinationEmpty', Ember.none(this.get('notificationDestination')));
    this.set('dateEmpty', Ember.none(this.get('expiryDate')));

    if (Ember.none(this.get('expiryDate'))) {
      date = null;
    } else {
      date = Date.parse(this.get('expiryDate'));
    }
    if (this.get('optionEmpty') || this.get('typeEmpty') || this.get('destinationEmpty') || this.get('dateEmpty')) {
      // do nothing
    } else {
      FLOW.store.createRecord(FLOW.NotificationSubscription, {
        "notificationOption": this.notificationOption.get('value'),
        "notificationType": this.notificationType.get('value'),
        "expiryDate": date,
        "notificationDestination": this.get('notificationDestination'),
        "notificationMethod": "EMAIL",
        "entityId": FLOW.selectedControl.selectedSurvey.get('keyId')
      });
      this.set('notificationOption', null);
      this.set('notificationType', null);
      this.set('notificationDestination', null);
      this.set('expiryDate', null);
      FLOW.store.commit();
    }
  },

  cancelNotification: function () {
    this.set('notificationEvent', null);
    this.set('notificationType', null);
    this.set('notificationDestination', null);
    this.set('expiryDate', null);
  },

  closeNotifications: function (router, event) {
    this.get('parentView').set('manageNotifications', false);
  },

  removeNotification: function (event) {
    var nDeleteId, notification;
    nDeleteId = event.context.get('keyId');

    notification = FLOW.store.find(FLOW.NotificationSubscription, nDeleteId);
    notification.deleteRecord();
    FLOW.store.commit();
  }
});

});

loader.register('akvo-flow/views/surveys/preview-view', function(require) {
FLOW.PreviewView = FLOW.View.extend({
  templateName: 'navSurveys/preview-view',
  closePreviewPopup: function () {
    FLOW.previewControl.set('showPreviewPopup', false);
  }

});

FLOW.PreviewQuestionGroupView = FLOW.View.extend({
  QGcontent: null,

  init: function () {
    var qgId,QGcontent;
    this._super();
    qgId = this.content.get('keyId');
    QGcontent = FLOW.store.filter(FLOW.Question, function (item) {
      return item.get('questionGroupId') == qgId;
    });

    tmp = QGcontent.toArray();
    tmp.sort(function(a,b){
    	return a.get('order') - b.get('order');
    });
    this.set('QGcontent',tmp);
  }
});

FLOW.PreviewQuestionView = FLOW.View.extend({
  isTextType: false,
  isOptionType: false,
  isNumberType: false,
  isPhotoType: false,
  isVideoType: false,
  isBarcodeType: false,
  isGeoType: false,
  isGeoshapeType:false,
  isDateType: false,
  isCascadeType: false,
  levelNameList:[],
  isVisible: true,
  optionsList: [],
  optionChoice: null,
  answer: null,
  latitude: null,
  longitude: null,

  init: function () {
    var opList, opListArray, i, sizeList, qId, tempList, cascadeNames;
    this._super();

    this.set('isTextType', this.content.get('type') == 'FREE_TEXT');
    this.set('isOptionType', this.content.get('type') == 'OPTION');
    this.set('isNumberType', this.content.get('type') == 'NUMBER');
    this.set('isPhotoType', this.content.get('type') == 'PHOTO');
    this.set('isVideoType', this.content.get('type') == 'VIDEO');
    this.set('isBarcodeType', this.content.get('type') == 'BARCODE');
    this.set('isGeoType', this.content.get('type') == 'GEO');
    this.set('isGeoshapeType', this.content.get('type') == 'GEOSHAPE');
    this.set('isDateType', this.content.get('type') == 'DATE');
    this.set('isCascadeType', this.content.get('type') == 'CASCADE');

    // fill option list
    if (this.isOptionType) {
      qId = this.content.get('keyId');
      options = FLOW.store.filter(FLOW.QuestionOption, function (item) {
        return item.get('questionId') == qId;
      });

      optionArray = options.toArray();
      optionArray.sort(function (a, b) {
    	  return a.get('order') - b.get('order');
      });

      tempList = [];
      optionArray.forEach(function (item) {
        tempList.push(Ember.Object.create({
          isSelected: false,
          value: item.get('text')
        }));
      });

      if (this.content.get('allowOtherFlag')) {
        tempList.push(Ember.Object.create({
          isSelected: false,
          value: Ember.String.loc('_other')
        }));
      }
      this.set('optionsList', tempList);
    }
    if (this.isCascadeType) {
    	cascade = FLOW.store.find(FLOW.CascadeResource,this.content.get('cascadeResourceId'));
    	if (!Ember.empty(cascade)){
    		cascadeNames = cascade.get('levelNames');
    		for (i=0 ; i < cascade.get('numLevels'); i++){
    			this.levelNameList.push(cascadeNames[i]);
    		}
    	}
    }
  },

  checkVisibility: function () {
    var dependentAnswerArray;
    if (this.content.get('dependentFlag') && this.content.get('dependentQuestionId') !== null) {
      dependentAnswerArray = this.content.get('dependentQuestionAnswer').split('|');
      if (dependentAnswerArray.indexOf(FLOW.previewControl.answers[this.content.get('dependentQuestionId')]) > -1) {
        this.set('isVisible', true);
      } else {
        this.set('isVisible', false);
      }
    }
  }.observes('FLOW.previewControl.changed'),

  storeOptionChoice: function () {
    var keyId;
    keyId = this.content.get('keyId');
    FLOW.previewControl.answers[keyId] = this.get('optionChoice');
    FLOW.previewControl.toggleProperty('changed');
  }.observes('this.optionChoice'),

  storeAnswer: function () {
    var keyId;
    keyId = this.content.get('keyId');
    FLOW.previewControl.answers[keyId] = this.get('answer');
  }.observes('this.answer')
});

});

loader.register('akvo-flow/views/surveys/question-view', function(require) {
function sortByOrder(a , b) {
  return a.get('order') - b.get('order');
}

FLOW.QuestionView = FLOW.View.extend({
  templateName: 'navSurveys/question-view',
  content: null,
  variableName: null,
  text: null,
  tip: null,
  type: null,
  mandatoryFlag: null,
  minVal: null,
  maxVal: null,
  allowSign: null,
  allowDecimal: null,
  allowMultipleFlag: null,
  allowOtherFlag: null,
  allowExternalSources: false,
  localeNameFlag:false,
  localeLocationFlag:false,
  geoLocked: null,
  requireDoubleEntry: null,
  dependentFlag: false,
  dependentQuestion: null,
  includeInMap: null,
  allowPoints: true,
  allowLine: true,
  allowPolygon: true,
  questionValidationFailure: false,
  questionTooltipValidationFailure: false,
  caddisflyResourceUuid: null,

  showCaddisflyTests: function () {
      return FLOW.router.caddisflyResourceController.get("testsFileLoaded");
  }.property('FLOW.router.caddisflyResourceController.testsFileLoaded'),

  showMetaConfig: function () {
    return FLOW.Env.showMonitoringFeature;
  }.property('FLOW.Env.showMonitoringFeature'),

  amOpenQuestion: function () {
    var selected = FLOW.selectedControl.get('selectedQuestion');
    if (selected && this.get('content')) {
      var isOpen = (this.content.get('keyId') == FLOW.selectedControl.selectedQuestion.get('keyId'));
      return isOpen;
    } else {
      return false;
    }
  }.property('FLOW.selectedControl.selectedQuestion', 'content.keyId').cacheable(),

  amTextType: function () {
    if (this.type) {
      return this.type.get('value') == 'FREE_TEXT';
    } else {
      return false;
    }
  }.property('this.type').cacheable(),

  amOptionType: function () {
    return this.type && this.type.get('value') === 'OPTION';
  }.property('this.type'),

  amNumberType: function () {
    if (this.type) {
      return this.type.get('value') == 'NUMBER';
    } else {
      return false;
    }
  }.property('this.type').cacheable(),

  amBarcodeType: function () {
      if (this.type) {
          return this.type.get('value') === 'SCAN';
      } else {
          return false;
      }
  }.property('this.type').cacheable(),

  amFreeTextType: function () {
    if (this.type) {
      return this.type.get('value') == 'FREE_TEXT';
    } else {
      return false;
    }
  }.property('this.type').cacheable(),

  amGeoType: function () {
    if (this.type) {
      return this.type.get('value') == 'GEO';
    } else {
      return false;
    }
  }.property('this.type').cacheable(),

  amNumberType: function () {
    if (this.type) {
      return this.type.get('value') == 'NUMBER';
    } else {
      return false;
    }
  }.property('this.type').cacheable(),

  amCascadeType: function () {
    if (this.type) {
	  return this.type.get('value') == 'CASCADE';
	} else {
	  return false;
	}
  }.property('this.type').cacheable(),

  hasExtraSettings: function () {
    var val;
    if (!Ember.none(this.type)) {
      val = this.type.get('value');
      return val === 'GEOSHAPE' || val === 'CASCADE' || val === 'NUMBER' || val === 'GEO' 
      || val === 'FREE_TEXT' || val === 'SCAN' || val === 'OPTION' || val === 'CADDISFLY';
    }
  }.property('this.type').cacheable(),

  amGeoshapeType: function () {
    if (this.type) {
	  return this.type.get('value') == 'GEOSHAPE';
	} else {
	  return false;
	}
  }.property('this.type').cacheable(),

  amDateType: function () {
    if (this.type) {
      return this.type.get('value') == 'DATE';
    } else {
      return false;
    }
  }.property('this.type').cacheable(),

  amSignatureType: function () {
    return (this.content && this.content.get('type') === 'SIGNATURE')
            || (this.type && this.type.get('value') === 'SIGNATURE');
  }.property('this.type'),
  
   amCaddisflyType: function(){
       return this.type && this.type.get('value') == 'CADDISFLY';
   }.property('this.type').cacheable(),
  
  showLocaleName: function () {
    if (!this.type) {
      return false;
    }
    return this.type.get('value') == 'FREE_TEXT'
        || this.type.get('value') == 'NUMBER'
        || this.type.get('value') == 'OPTION'
        || this.type.get('value') == 'CASCADE';
  }.property('this.type').cacheable(),

  // TODO dependencies
  // TODO options
  doQuestionEdit: function () {
    var questionType = null,
    dependentQuestion, dependentAnswer, dependentAnswerArray,cascadeResource;
    if (this.content && (this.content.get('isDirty') || this.content.get('isSaving'))) {
      this.showMessageDialog(Ember.String.loc('_question_is_being_saved'),
			     Ember.String.loc('_question_is_being_saved_text'));
      return;
    }

    this.loadQuestionOptions();

    FLOW.selectedControl.set('selectedQuestion', this.get('content'));
    this.set('variableName', FLOW.selectedControl.selectedQuestion.get('variableName'));
    this.set('text', FLOW.selectedControl.selectedQuestion.get('text'));
    this.set('tip', FLOW.selectedControl.selectedQuestion.get('tip'));
    this.set('mandatoryFlag', FLOW.selectedControl.selectedQuestion.get('mandatoryFlag'));
    this.set('minVal', FLOW.selectedControl.selectedQuestion.get('minVal'));
    this.set('maxVal', FLOW.selectedControl.selectedQuestion.get('maxVal'));
    this.set('allowSign', FLOW.selectedControl.selectedQuestion.get('allowSign'));
    this.set('allowDecimal', FLOW.selectedControl.selectedQuestion.get('allowDecimal'));
    this.set('allowMultipleFlag', FLOW.selectedControl.selectedQuestion.get('allowMultipleFlag'));
    this.set('allowOtherFlag', FLOW.selectedControl.selectedQuestion.get('allowOtherFlag'));
    this.set('allowExternalSources', FLOW.selectedControl.selectedQuestion.get('allowExternalSources'));
    this.set('localeNameFlag', FLOW.selectedControl.selectedQuestion.get('localeNameFlag'));
    this.set('localeLocationFlag', FLOW.selectedControl.selectedQuestion.get('localeLocationFlag'));
    this.set('geoLocked', FLOW.selectedControl.selectedQuestion.get('geoLocked'));
    this.set('requireDoubleEntry', FLOW.selectedControl.selectedQuestion.get('requireDoubleEntry'));
    this.set('includeInMap', FLOW.selectedControl.selectedQuestion.get('includeInMap'));
    this.set('dependentFlag', FLOW.selectedControl.selectedQuestion.get('dependentFlag'));
    this.set('allowPoints', FLOW.selectedControl.selectedQuestion.get('allowPoints'));
    this.set('allowLine', FLOW.selectedControl.selectedQuestion.get('allowLine'));
    this.set('allowPolygon', FLOW.selectedControl.selectedQuestion.get('allowPolygon'));
    this.set('cascadeResourceId', FLOW.selectedControl.selectedQuestion.get('cascadeResourceId'));
    this.set('caddisflyResourceUuid', FLOW.selectedControl.selectedQuestion.get('caddisflyResourceUuid'));

    FLOW.optionListControl.set('content', []);

    // if the cascadeResourceId is not null, get the resource
    if (!Ember.empty(FLOW.selectedControl.selectedQuestion.get('cascadeResourceId'))) {
    	cascadeResource = FLOW.store.find(FLOW.CascadeResource,FLOW.selectedControl.selectedQuestion.get('cascadeResourceId'));
    	FLOW.selectedControl.set('selectedCascadeResource', cascadeResource);
    }

    // reset selected caddisfly resource
    FLOW.selectedControl.set('selectedCaddisflyResource', null);
    // if the caddisflyResourceUuid is not null, get the resource
    if (!Ember.empty(FLOW.selectedControl.selectedQuestion.get('caddisflyResourceUuid'))) {
      var caddResource = FLOW.router.caddisflyResourceController.content.findProperty('uuid', FLOW.selectedControl.selectedQuestion.get('caddisflyResourceUuid'));
      if (!Ember.empty(caddResource)) {
        FLOW.selectedControl.set('selectedCaddisflyResource',caddResource);
      }
    }
    // if the dependentQuestionId is not null, get the question
    if (!Ember.empty(FLOW.selectedControl.selectedQuestion.get('dependentQuestionId'))) {
      dependentQuestion = FLOW.store.find(FLOW.Question, FLOW.selectedControl.selectedQuestion.get('dependentQuestionId'));
      dependentAnswer = FLOW.selectedControl.selectedQuestion.get('dependentQuestionAnswer');

      // if we have found the question, fill the options
      if (dependentQuestion.get('id') !== "0") {
        FLOW.selectedControl.set('dependentQuestion', dependentQuestion);
        this.fillOptionList();

        dependentAnswerArray = dependentAnswer.split('|');
        // find the answer already set and set it to true in the optionlist
        FLOW.optionListControl.get('content').forEach(function (item) {
          if (dependentAnswerArray.indexOf(item.get('value')) > -1) {
            item.set('isSelected', true);
          }
        });
      }
    }

    // set the type to the original choice
    FLOW.questionTypeControl.get('content').forEach(function (item) {
      if (item.get('value') == FLOW.selectedControl.selectedQuestion.get('type')) {
        questionType = item;
      }
    });
    this.set('type', questionType);
  },

  /*
   *  Load the question options for question editing
   */
  loadQuestionOptions: function () {
    var c = this.content;
    FLOW.questionOptionsControl.set('content', []);
    FLOW.questionOptionsControl.set('questionId', c.get('keyId'));

    options = FLOW.store.filter(FLOW.QuestionOption, function (optionItem) {
        return optionItem.get('questionId') === c.get('keyId');
    });

    if (options.get('length')) {
      optionArray = Ember.A(options.toArray().sort(sortByOrder));
      FLOW.questionOptionsControl.set('content', optionArray);
    } else {
      FLOW.questionOptionsControl.loadDefaultOptions();
    }
  },

  fillOptionList: function () {
    var optionList, optionListArray, i, sizeList;
    if (FLOW.selectedControl.get('dependentQuestion') !== null) {
      FLOW.optionListControl.set('content', []);
      FLOW.optionListControl.set('currentActive', null);

      options = FLOW.store.filter(FLOW.QuestionOption, function (item) {
        if (!Ember.none(FLOW.selectedControl.selectedQuestion)) {
          return item.get('questionId') == FLOW.selectedControl.dependentQuestion.get('keyId');
        } else {
          return false;
        }
      });

      optionArray = options.toArray();
      optionArray.sort(function (a, b) {
    	  return a.get('order') - b.get('order');
      });

      optionArray.forEach(function (item) {
        FLOW.optionListControl.get('content').push(Ember.Object.create({
          isSelected: false,
          value: item.get('text')
        }));
      });
    }
  }.observes('FLOW.selectedControl.dependentQuestion'),

  doCancelEditQuestion: function () {
    FLOW.selectedControl.set('selectedQuestion', null);
  },


  doSaveEditQuestion: function() {
    var path, anyActive, first, dependentQuestionAnswer, minVal, maxVal, options, found, optionsToDelete;

    if (this.variableNameValidationFailure) {
      this.showMessageDialog(Ember.String.loc('_variable_name_must_be_valid_and_unique'), this.variableNameValidationFailureReason);
      return;
    }

    if (this.questionValidationFailure) {
        this.showMessageDialog(Ember.String.loc('_question_over_500_chars_header'), Ember.String.loc('_question_over_500_chars_text'));
        return;
      }

    if (this.questionTooltipValidationFailure) {
        this.showMessageDialog(Ember.String.loc('_tooltip_over_500_chars_header'), Ember.String.loc('_tooltip_over_500_chars_text'));
        return;
      }

    if (this.get('amOptionType')) {
      var invalidOptions = FLOW.questionOptionsControl.validateOptions();
      if (invalidOptions) {
        this.showMessageDialog(Ember.String.loc('_invalid_options_header'), invalidOptions);
        return;
      }

      // save options to the datastore
      FLOW.questionOptionsControl.persistOptions();
    }

    if (this.type.get('value') === 'CASCADE' && Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'))) {
        FLOW.dialogControl.set('activeAction', 'ignore');
        FLOW.dialogControl.set('header', Ember.String.loc('_cascade_resources'));
        FLOW.dialogControl.set('message', Ember.String.loc('_cascade_select_resource'));
        FLOW.dialogControl.set('showCANCEL', false);
        FLOW.dialogControl.set('showDialog', true);
        return false;
    }

    if (this.type.get('value') !== 'NUMBER') {
      this.set('minVal', null);
      this.set('maxVal', null);
      this.set('allowSign', false);
      this.set('allowDecimal', false);
    }
    if (this.type.get('value') !== 'GEO' && this.type.get('value') !== 'GEOSHAPE' && this.type.get('value') !== 'SCAN') {
      this.set('geoLocked', false);
    }

    if (!(this.type.get('value') == 'NUMBER' || this.type.get('value') == 'FREE_TEXT')) {
      this.set('requireDoubleEntry', false);
    }

    if (!(this.type.get('value') == 'CASCADE')) {
      this.set('cascadeResourceId', null);
    }

    if (!(this.type.get('value') == 'CADDISFLY')) {
      this.set('caddisflyResourceUuid', null);
    }

    path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name') + "/" + FLOW.selectedControl.selectedQuestionGroup.get('code');
    FLOW.selectedControl.selectedQuestion.set('variableName', this.get('variableName'));
    FLOW.selectedControl.selectedQuestion.set('text', this.get('text'));
    FLOW.selectedControl.selectedQuestion.set('tip', this.get('tip'));
    FLOW.selectedControl.selectedQuestion.set('mandatoryFlag', this.get('mandatoryFlag'));

    minVal = (Ember.empty(this.get('minVal'))) ? null : this.get('minVal');
    maxVal = (Ember.empty(this.get('maxVal'))) ? null : this.get('maxVal');
    FLOW.selectedControl.selectedQuestion.set('minVal', minVal);
    FLOW.selectedControl.selectedQuestion.set('maxVal', maxVal);

    FLOW.selectedControl.selectedQuestion.set('path', path);
    FLOW.selectedControl.selectedQuestion.set('allowSign', this.get('allowSign'));
    FLOW.selectedControl.selectedQuestion.set('allowDecimal', this.get('allowDecimal'));
    FLOW.selectedControl.selectedQuestion.set('allowMultipleFlag', this.get('allowMultipleFlag'));
    FLOW.selectedControl.selectedQuestion.set('allowOtherFlag', this.get('allowOtherFlag'));
    FLOW.selectedControl.selectedQuestion.set('localeNameFlag', this.get('localeNameFlag'));
    FLOW.selectedControl.selectedQuestion.set('localeLocationFlag', this.get('localeLocationFlag'));
    FLOW.selectedControl.selectedQuestion.set('geoLocked', this.get('geoLocked'));
    FLOW.selectedControl.selectedQuestion.set('requireDoubleEntry', this.get('requireDoubleEntry'));
    FLOW.selectedControl.selectedQuestion.set('includeInMap', this.get('includeInMap'));
    FLOW.selectedControl.selectedQuestion.set('allowPoints', this.get('allowPoints'));
    FLOW.selectedControl.selectedQuestion.set('allowLine', this.get('allowLine'));
    FLOW.selectedControl.selectedQuestion.set('allowPolygon', this.get('allowPolygon'));

    var allowExternalSources = (this.type.get('value') !== 'FREE_TEXT') ? false : this.get('allowExternalSources');
    FLOW.selectedControl.selectedQuestion.set('allowExternalSources', allowExternalSources);

    dependentQuestionAnswer = "";
    first = true;

    FLOW.optionListControl.get('content').forEach(function (item) {
      if (item.isSelected) {
        if (!first) {
          dependentQuestionAnswer += "|";
        }
        first = false;
        dependentQuestionAnswer += item.value;
      }
    });

    if (this.get('dependentFlag') && dependentQuestionAnswer !== "") {
      FLOW.selectedControl.selectedQuestion.set('dependentFlag', this.get('dependentFlag'));
      FLOW.selectedControl.selectedQuestion.set('dependentQuestionId', FLOW.selectedControl.dependentQuestion.get('keyId'));
      FLOW.selectedControl.selectedQuestion.set('dependentQuestionAnswer', dependentQuestionAnswer);
    } else {
      FLOW.selectedControl.selectedQuestion.set('dependentFlag', false);
      FLOW.selectedControl.selectedQuestion.set('dependentQuestionId', null);
      FLOW.selectedControl.selectedQuestion.set('dependentQuestionAnswer', null);
    }

    if (this.get('type')) {
      FLOW.selectedControl.selectedQuestion.set('type', this.type.get('value'));
    }

    // deal with cascadeResource
    if (this.type.get('value') == 'CASCADE') {
        if (!Ember.empty(FLOW.selectedControl.get('selectedCascadeResource'))){
            FLOW.selectedControl.selectedQuestion.set('cascadeResourceId',
                FLOW.selectedControl.selectedCascadeResource.get('keyId'));
        }
    }

    // deal with caddisflyResource
    if (this.type.get('value') == 'CADDISFLY') {
      if (!Ember.empty(FLOW.selectedControl.get('selectedCaddisflyResource'))){
        FLOW.selectedControl.selectedQuestion.set('caddisflyResourceUuid',
            FLOW.selectedControl.selectedCaddisflyResource.get('uuid'));
      }
    }

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedQuestion', null);
    FLOW.selectedControl.set('dependentQuestion', null);
    FLOW.selectedControl.set('selectedCascadeResource',null);
  },

  isPartOfMonitoringGroup: function(questionKeyId) {
    var surveyId = FLOW.store.findById(FLOW.Question, questionKeyId).get('surveyId');
    var surveyGroupId = FLOW.store.findById(FLOW.Survey, surveyId).get('surveyGroupId');
    return FLOW.store.findById(FLOW.SurveyGroup, surveyGroupId).get('monitoringGroup');
  },

  /**
   * Variable name validation
   *
   * A valid variable name must match /^[A-Za-z0-9_\-]*$/. Uniqueness
   * constraints depends on wether the question is part of a
   * monitoring group or not. If the question is part of a
   * monitoring group, uniqueness validation _must_ happen on the
   * server and cover all questions which are part of that group. If
   * not, the uniqueness constraint only covers the survey and can
   * be checked on the client.
   */
  throttleTimer: null,

  validateVariableName: function(args) {
    var self = this;
    var selectedQuestion = FLOW.selectedControl.selectedQuestion;
    var questionKeyId = selectedQuestion.get('keyId');
    var variableName = this.get('variableName') || "";
    if (FLOW.Env.mandatoryQuestionID && variableName.match(/^\s*$/)) {
      args.failure(Ember.String.loc('_variable_name_mandatory'));
    } else if (!variableName.match(/^[A-Za-z0-9_\-]*$/)) {
      args.failure(Ember.String.loc('_variable_name_only_alphanumeric'))
    } else {
      var monitoring = this.isPartOfMonitoringGroup(questionKeyId);
      if (monitoring) {
        clearTimeout(this.throttleTimer);
        this.throttleTimer = setTimeout(function () {
          $.ajax({
            url: '/rest/questions/' + questionKeyId + '/validate?variableName=' + variableName,
            type: 'POST',
            success: function(data) {
              if (data.success) {
                //check for special characters once more
                if (!self.get('variableName').match(/^[A-Za-z0-9_\-]*$/)) {
                  args.failure(Ember.String.loc('_variable_name_only_alphanumeric'));
                } else {
                  args.success();
                }
              } else {
                args.failure(data.reason);
              }
            },
            error: function() {
              args.failure(Ember.String.loc('_could_not_validate_variable_name_with_server'));
            }
          });
        }, 1000);
      } else {
        var otherVariableNames = FLOW.store.filter(FLOW.Question, function(question) {
          return (selectedQuestion.get('surveyId') === question.get('surveyId'))
            && (questionKeyId !== question.get('keyId'));
        }).map(function(question) {
          return question.get('variableName');
        }).filter(function(variableName) {
          return variableName !== "";
        });
        var isUnique = !otherVariableNames.contains(variableName);
        if (isUnique) {
          args.success();
        } else {
          args.failure(Ember.String.loc('_variable_name_not_unique'));
        }
      }
    }
  },

  validateMinAndMax: function(args) {
    if (this.type.get('value') == 'NUMBER') {
      if (!Ember.empty(this.get('minVal')) && !Ember.empty(this.get('maxVal'))) {
        if (isNaN(this.get('minVal')) || isNaN(this.get('maxVal'))) {
	  args.NaNFailure();
	  return;
        } else if (parseFloat(this.get('minVal')) >= parseFloat(this.get('maxVal'))) {
          args.valueFailure();
	  return;
        }
      }
    }
    args.success();
  },

  showMessageDialog: function(header, message) {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', header);
    FLOW.dialogControl.set('message', message);
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  deleteQuestion: function () {
    var qDeleteId;
    qDeleteId = this.content.get('keyId');

    // check if anything is being saved at the moment
    if (this.checkQuestionsBeingSaved()) {
      this.showMessageDialog(Ember.String.loc('_please_wait'),
			     Ember.String.loc('_please_wait_until_previous_request'));
      return;
    }

    // Check if there is another question that is dependant on this question
    if (this.content.get('type') === 'OPTION') {
      var hasDependant = FLOW.store.find(FLOW.Question).some(function (q) {
        return qDeleteId === q.get('dependentQuestionId');
      });

      if (hasDependant) {
        this.showMessageDialog(
          Ember.String.loc('_cant_delete_question'),
          Ember.String.loc('_another_question_depends_on_this'));
        return;
      }
    }

    // check if deleting this question is allowed
    // if successful, the deletion action will be called from DS.FLOWrestadaptor.sideload
    FLOW.store.findQuery(FLOW.Question, {
      preflight: 'delete',
      questionId: qDeleteId
    });
  },

  checkQuestionsBeingSaved: function () {
    var question;
    question = FLOW.store.filter(FLOW.Question, function(item){
      return item.get('isSaving');
    });
    return question.content.length > 0;
  },

  // move question to selected location
  doQuestionMoveHere: function () {
    var selectedOrder, insertAfterOrder, selectedQ, useMoveQuestion, qgIdSource, qgIdDest;
    selectedOrder = FLOW.selectedControl.selectedForMoveQuestion.get('order');

    if (this.get('zeroItemQuestion')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }

    // check if anything is being saved at the moment
    if (this.checkQuestionsBeingSaved()) {
      this.showMessageDialog(Ember.String.loc('_please_wait'),
			     Ember.String.loc('_please_wait_until_previous_request'));
      return;
    }

    // check to see if we are trying to move the question to another question group
    if (FLOW.selectedControl.selectedForMoveQuestion.get('questionGroupId') != FLOW.selectedControl.selectedQuestionGroup.get('keyId')) {
      selectedQ = FLOW.store.find(FLOW.Question, FLOW.selectedControl.selectedForMoveQuestion.get('keyId'));
      if (selectedQ !== null) {

        qgIdSource = FLOW.selectedControl.selectedForMoveQuestion.get('questionGroupId');
        qgIdDest = FLOW.selectedControl.selectedQuestionGroup.get('keyId');

        // restore order
        FLOW.questionControl.reorderQuestions(qgIdSource, selectedOrder, "decrement");
        FLOW.questionControl.reorderQuestions(qgIdDest, insertAfterOrder, "increment");

        // move question
        selectedQ.set('order', insertAfterOrder + 1);
        selectedQ.set('questionGroupId', qgIdDest);

        FLOW.questionControl.submitBulkQuestionsReorder([qgIdSource, qgIdDest]);
      }
    // if we are not moving to another group, we must be moving inside a group
    // only do something if we are not moving to the same place
    } else if (!((selectedOrder == insertAfterOrder) || (selectedOrder == (insertAfterOrder + 1)))) {
      selectedQ = FLOW.store.find(FLOW.Question, FLOW.selectedControl.selectedForMoveQuestion.get('keyId'));
      if (selectedQ !== null) {
        // restore order
        qgId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');
        questionsInGroup = FLOW.store.filter(FLOW.Question, function (item) {
          return item.get('questionGroupId') == qgId;
        });

        origOrder = FLOW.selectedControl.selectedForMoveQuestion.get('order');
        movingUp = origOrder < insertAfterOrder;

        questionsInGroup.forEach(function (item) {
          currentOrder = item.get('order');
          if (movingUp) {
            if (currentOrder == origOrder) {
              // move moving item to right location
              selectedQ.set('order', insertAfterOrder);
            } else if ((currentOrder > origOrder) && (currentOrder <= insertAfterOrder)) {
              // move item down
              item.set('order', item.get('order') - 1);
            }
          } else {
            // Moving down
            if (currentOrder == origOrder) {
              // move moving item to right location
              selectedQ.set('order', insertAfterOrder + 1);
            } else if ((currentOrder < origOrder) && (currentOrder > insertAfterOrder)) {
              // move item up
              item.set('order', item.get('order') + 1);
            }
          }
        });

        FLOW.questionControl.submitBulkQuestionsReorder([qgId]);
      }
    }
    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedForMoveQuestion', null);
  },

  // execute question copy to selected location
  doQuestionCopyHere: function () {
    var insertAfterOrder, path, qgId, question;
    //path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name') + "/" + FLOW.selectedControl.selectedQuestionGroup.get('code');

    if (this.get('zeroItemQuestion')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }

    // check if anything is being saved at the moment
    if (this.checkQuestionsBeingSaved()) {
      this.showMessageDialog(Ember.String.loc('_please_wait'),
			     Ember.String.loc('_please_wait_until_previous_request'));
      return;
    }

    qgId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');

    // restore order
    FLOW.questionControl.reorderQuestions(qgId, insertAfterOrder, "increment");

    question = FLOW.selectedControl.get('selectedForCopyQuestion');
    // create copy of Question item in the store
    FLOW.store.createRecord(FLOW.Question, {
      "order": insertAfterOrder + 1,
      "surveyId": question.get('surveyId'),
      "questionGroupId": qgId,
      "sourceId":question.get('keyId')
    });

    FLOW.questionControl.submitBulkQuestionsReorder([qgId]);

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedForCopyQuestion', null);
  },

  // create new question
  doInsertQuestion: function () {
    var insertAfterOrder, path, qgId;
    path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name') + "/" + FLOW.selectedControl.selectedQuestionGroup.get('code');

    if (this.get('zeroItemQuestion')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }

    // check if anything is being saved at the moment
    if (this.checkQuestionsBeingSaved()) {
      this.showMessageDialog(Ember.String.loc('_please_wait'),
			     Ember.String.loc('_please_wait_until_previous_request'));
      return;
    }


    qgId = FLOW.selectedControl.selectedQuestionGroup.get('keyId');

    // reorder the rest of the questions
    FLOW.questionControl.reorderQuestions(qgId, insertAfterOrder, "increment");

    // create new Question item in the store
    FLOW.store.createRecord(FLOW.Question, {
      "order": insertAfterOrder + 1,
      "type": "FREE_TEXT",
      "path": path,
      "text": Ember.String.loc('_new_question_please_change_name'),
      "surveyId": FLOW.selectedControl.selectedSurvey.get('keyId'),
      "questionGroupId": qgId
    });

    FLOW.questionControl.submitBulkQuestionsReorder([qgId]);

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
  },

  // true if one question has been selected for Move
  oneSelectedForMove: function () {
    var selectedForMove = FLOW.selectedControl.get('selectedForMoveQuestion');
    if (selectedForMove) {
      return true;
    } else {
      return false;
    }
  }.property('FLOW.selectedControl.selectedForMoveQuestion'),

  // true if one question has been selected for Copy
  oneSelectedForCopy: function () {
    var selectedForCopy = FLOW.selectedControl.get('selectedForCopyQuestion');
    if (selectedForCopy) {
      return true;
    } else {
      return false;
    }
  }.property('FLOW.selectedControl.selectedForCopyQuestion'),

  // prepare for question copy. Shows 'copy to here' buttons
  doQuestionCopy: function () {
    FLOW.selectedControl.set('selectedForCopyQuestion', this.get('content'));
    FLOW.selectedControl.set('selectedForMoveQuestion', null);
  },

  // cancel question copy
  doQuestionCopyCancel: function () {
    FLOW.selectedControl.set('selectedForCopyQuestion', null);
  },

  // prepare for question move. Shows 'move here' buttons
  doQuestionMove: function () {
    FLOW.selectedControl.set('selectedForMoveQuestion', this.get('content'));
    FLOW.selectedControl.set('selectedForCopyQuestion', null);
  },

  // cancel group move
  doQuestionMoveCancel: function () {
    FLOW.selectedControl.set('selectedForMoveQuestion', null);
  },

  validateQuestionObserver: function () {
      this.set('questionValidationFailure', ((this.text && this.text.length > 500) || !this.text || this.text == ""));
      if (this.text && this.text.length > 500) {
        this.set('questionValidationFailureReason', Ember.String.loc('_question_over_500_chars_header'));
      } else {
        if (!this.text || this.text == "") {
          this.set('questionValidationFailureReason', Ember.String.loc('_question_text_empty'));
        }
      }
  }.observes('this.text'),

  validateQuestionTooltipObserver: function(){
      this.set('questionTooltipValidationFailure', (this.tip != null && this.tip.length > 500));
  }.observes('this.tip'),

  validateVariableNameObserver: function() {
    var self = this;
    self.validateVariableName({
      success: function() {
        self.set('variableNameValidationFailure', false);
        self.set('variableNameValidationFailureReason', null);
      },
      failure: function(msg) {
        self.set('variableNameValidationFailure', true);
        self.set('variableNameValidationFailureReason', msg);
      }
    });
  }.observes('this.variableName'),

  showQuestionModifyButtons: function () {
    var form = FLOW.selectedControl.get('selectedSurvey');
    return FLOW.permControl.canEditForm(form);
  }.property('FLOW.selectedControl.selectedSurvey'),
});

/*
 *  View to render the options for an option type question.
 */
FLOW.OptionListView = Ember.CollectionView.extend({
  tagName: 'ul',
  content: null,
  itemViewClass: Ember.View.extend({
    templateName: 'navSurveys/question-option',
  }),
});

});

loader.register('akvo-flow/views/surveys/survey-details-views', function(require) {
// ************************ Surveys *************************
// FLOW.SurveySidebarView = FLOW.View.extend({
FLOW.SurveySidebarView = FLOW.View.extend({
  surveyTitle: null,
  surveyDescription: null,
  surveyPointType: null,
  language: null,
  isDirty: false,

  init: function () {
    var pointType = null,
      language = null;
    this._super();
    this.set('surveyTitle', FLOW.selectedControl.selectedSurvey.get('name'));
    this.set('surveyDescription', FLOW.selectedControl.selectedSurvey.get('description'));

    FLOW.surveyPointTypeControl.get('content').forEach(function (item) {
      if (item.get('value') == FLOW.selectedControl.selectedSurvey.get('pointType')) {
        pointType = item;
      }
    });
    this.set('surveyPointType', pointType);
    FLOW.translationControl.get('isoLangs').forEach(function (item) {
      if (item.get('value') == FLOW.selectedControl.selectedSurvey.get('defaultLanguageCode')) {
        language = item;
      }
    });
    this.set('language', language);
  },

  isExistingSurvey: function () {
    return !Ember.none(FLOW.selectedControl.selectedSurvey.get('keyId'));
  }.property('FLOW.selectedControl.selectedSurvey.keyId'),

  setIsDirty: function () {
    var isDirty, survey;
    survey = FLOW.selectedControl.get('selectedSurvey');
    isDirty = this.get('surveyTitle') != survey.get('name');

    if (!Ember.none(this.get('surveyDescription'))) {
      isDirty = isDirty || this.get('surveyDescription') != survey.get('description');
    } else {
      // if we don't have one now, but we had one before, it has also changed
      isDirty = isDirty || !Ember.none(survey.get('surveyDescription'));
    }

    if (!Ember.none(this.get('surveyPointType'))) {
      // if we have a surveyPointType, compare them
      isDirty = isDirty || this.surveyPointType.get('value') != survey.get('pointType');
    } else {
      isDirty = isDirty || this.get('surveyPointType') === null;
      // if we don't have one now, but we had one before, it has also changed
      // TODO - this breaks when the pointType is an old point Type
      //isDirty = isDirty || !Ember.none(survey.get('pointType'));
    }

    if (!Ember.none(this.get('language'))) {
      isDirty = isDirty || this.language.get('value') != survey.get('defaultLanguageCode');
    } else {
      isDirty = isDirty || !Ember.empty(survey.get('defaultLanguageCode'));
    }
    this.set('isDirty', isDirty);
  },

  isPublished: function () {
    return FLOW.selectedControl.selectedSurvey.get('status') == 'PUBLISHED';
  }.property('FLOW.selectedControl.selectedSurvey.status'),

  numberQuestions: function () {
    if (Ember.none(FLOW.questionControl.get('filterContent'))) {
      return 0;
    }
    return FLOW.questionControl.filterContent.toArray().length;
  }.property('FLOW.questionControl.filterContent.@each'),

  numberQuestionGroups: function () {
    if (Ember.none(FLOW.questionGroupControl.get('content'))) {
      return 0;
    }
    return FLOW.questionGroupControl.content.toArray().length;
  }.property('FLOW.questionGroupControl.content.@each'),

  surveyNotComplete: function () {
	 if (Ember.empty(this.get('surveyTitle'))) {
		 FLOW.dialogControl.set('activeAction', 'ignore');
		 FLOW.dialogControl.set('header', Ember.String.loc('_survey_title_not_set'));
		 FLOW.dialogControl.set('message', Ember.String.loc('_survey_title_not_set_text'));
		 FLOW.dialogControl.set('showCANCEL', false);
		 FLOW.dialogControl.set('showDialog', true);
		 return true;
	 }
	 if (Ember.empty(this.get('surveyPointType'))) {
		 FLOW.dialogControl.set('activeAction', 'ignore');
		 FLOW.dialogControl.set('header', Ember.String.loc('_survey_type_not_set'));
		 FLOW.dialogControl.set('message', Ember.String.loc('_survey_type_not_set_text'));
		 FLOW.dialogControl.set('showCANCEL', false);
		 FLOW.dialogControl.set('showDialog', true);
		 return true;
	 }
	 return false;
  },

  doManageTranslations: function () {
	// check if we have questions that are still loading
	if (Ember.none(FLOW.questionControl.get('content'))){
	  	FLOW.dialogControl.set('activeAction', "ignore");
	  	FLOW.dialogControl.set('header', Ember.String.loc('_no_questions'));
	  	FLOW.dialogControl.set('message', Ember.String.loc('_no_questions_text'));
	  	FLOW.dialogControl.set('showCANCEL', false);
	  	FLOW.dialogControl.set('showDialog', true);
	    return;
	}
	// check if we have questions that are still loading
	if (!FLOW.questionControl.content.get('isLoaded')){
  		FLOW.dialogControl.set('activeAction', "ignore");
  	    FLOW.dialogControl.set('header', Ember.String.loc('_questions_still_loading'));
  	    FLOW.dialogControl.set('message', Ember.String.loc('_questions_still_loading_text'));
  	    FLOW.dialogControl.set('showCANCEL', false);
  	    FLOW.dialogControl.set('showDialog', true);
  		return;
  	}
	if (this.surveyNotComplete()){
		return;
	}
	// check if we have any unsaved changes
	survey = FLOW.store.find(FLOW.Survey, FLOW.selectedControl.selectedSurvey.get('keyId'));
	this.setIsDirty();
	if (!Ember.none(survey) && this.get('isDirty')) {
	    FLOW.dialogControl.set('activeAction', "ignore");
	    FLOW.dialogControl.set('header', Ember.String.loc('_you_have_unsaved_changes'));
	    FLOW.dialogControl.set('message', Ember.String.loc('_before_translations_save'));
	    FLOW.dialogControl.set('showCANCEL', false);      FLOW.dialogControl.set('showDialog', true);
	    return;
	}
	FLOW.router.transitionTo('navSurveys.navSurveysEdit.manageTranslations');
  },


  doManageNotifications: function () {
	if (this.surveyNotComplete()){
		return;
	}
	// check if we have any unsaved changes
	survey = FLOW.store.find(FLOW.Survey, FLOW.selectedControl.selectedSurvey.get('keyId'));
	this.setIsDirty();
	if (!Ember.none(survey) && this.get('isDirty')) {
		 FLOW.dialogControl.set('activeAction', "ignore");
		 FLOW.dialogControl.set('header', Ember.String.loc('_you_have_unsaved_changes'));
		 FLOW.dialogControl.set('message', Ember.String.loc('_before_notifications_save'));
		 FLOW.dialogControl.set('showCANCEL', false);      FLOW.dialogControl.set('showDialog', true);
		 return;
	}
	FLOW.router.transitionTo('navSurveys.navSurveysEdit.manageNotifications');
  },

  doSaveSurvey: function () {
    var survey, re = /,/g;
    if (this.surveyNotComplete()){
		return;
	}
    survey = FLOW.selectedControl.get('selectedSurvey');

    // Silently replace commas (,)
    // See: https://github.com/akvo/akvo-flow/issues/707
    survey.set('name', this.get('surveyTitle').replace(re, ' '));
    survey.set('code', this.get('surveyTitle').replace(re, ' '));

    survey.set('status', 'NOT_PUBLISHED');
    survey.set('path', FLOW.selectedControl.selectedSurveyGroup.get('code'));
    survey.set('description', this.get('surveyDescription'));
    if (this.get('surveyPointType') !== null) {
      survey.set('pointType', this.surveyPointType.get('value'));
    } else {
      survey.set('pointType', null);
    }
    if (this.get('language') !== null) {
      survey.set('defaultLanguageCode', this.language.get('value'));
    } else {
      survey.set('defaultLanguageCode', null);
    }
    FLOW.store.commit();
  },

  doPreviewSurvey: function () {
    FLOW.previewControl.set('showPreviewPopup', true);
  },

  doPublishSurvey: function () {
    var survey;
    // validation
    if (this.get('surveyPointType') === null) {
      FLOW.dialogControl.set('activeAction', 'ignore');
      FLOW.dialogControl.set('header', Ember.String.loc('_survey_type_not_set'));
      FLOW.dialogControl.set('message', Ember.String.loc('_survey_type_not_set_text'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
      return;
    }

    // check if survey has unsaved changes
    survey = FLOW.store.find(FLOW.Survey, FLOW.selectedControl.selectedSurvey.get('keyId'));
    this.setIsDirty();
    if (!Ember.none(survey) && this.get('isDirty')) {
      FLOW.dialogControl.set('activeAction', "ignore");
      FLOW.dialogControl.set('header', Ember.String.loc('_you_have_unsaved_changes'));
      FLOW.dialogControl.set('message', Ember.String.loc('_before_publishing_'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);

    } else {
      FLOW.surveyControl.publishSurvey();
      FLOW.dialogControl.set('activeAction', "ignore");
      FLOW.dialogControl.set('header', Ember.String.loc('_publishing_survey'));
      FLOW.dialogControl.set('message', Ember.String.loc('_survey_published_text_'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
    }
  },

  doSurveysMain: function () {
    var item;
    // if the survey does not have a keyId, it has not been saved, so delete it.
    if (Ember.none(FLOW.selectedControl.selectedSurvey.get('keyId'))) {
      item = FLOW.selectedControl.get('selectedSurvey');
      item.deleteRecord();
    }
    FLOW.selectedControl.set('selectedQuestionGroup', null);
    FLOW.selectedControl.set('selectedSurvey', null);
    FLOW.surveyControl.refresh();
    FLOW.router.transitionTo('navSurveys.navSurveysMain');
  }
});

FLOW.QuestionGroupItemTranslationView = FLOW.View.extend({
	content: null,
	 // question group content comes through binding in handlebars file
	amVisible: function () {
	  var selected, isVis;
	  selected = FLOW.selectedControl.get('selectedQuestionGroup');
	  if (selected) {
	     isVis = (this.content.get('keyId') === FLOW.selectedControl.selectedQuestionGroup.get('keyId'));
	     return isVis;
	   } else {
	     return null;
	   }
	 }.property('FLOW.selectedControl.selectedQuestionGroup', 'content.keyId').cacheable(),

	toggleVisibility: function () {
	   if (this.get('amVisible')) {
		 // if we have any unsaved translations, do nothing.
		 // a warning will be printed by the check method.
		   console.log('unsaved? ',FLOW.translationControl.unsavedTranslations());
		 if (FLOW.translationControl.unsavedTranslations()){
			 return;
		 }
	     FLOW.selectedControl.set('selectedQuestionGroup', null);
	     // empty translation structures
	   } else {
	     FLOW.selectedControl.set('selectedQuestionGroup', this.content);
	     FLOW.translationControl.loadQuestionGroup(this.content.get('keyId'));
	   }
	}
});


FLOW.QuestionGroupItemView = FLOW.View.extend({
  // question group content comes through binding in handlebars file
  zeroItem: false,
  renderView: false,
  showQGDeletedialog: false,
  showQGroupNameEditField: false,
  pollingTimer: null,
  showSaveCancelButton: false,

  amCopying: function(){
      return this.content.get('status') == "COPYING";
  }.property('this.content.status'),

  amVisible: function () {
    var selected, isVis;
    selected = FLOW.selectedControl.get('selectedQuestionGroup');
    if (selected) {

      isVis = (this.content.get('keyId') === FLOW.selectedControl.selectedQuestionGroup.get('keyId'));
      return isVis;
    } else {
      return null;
    }
  }.property('FLOW.selectedControl.selectedQuestionGroup', 'content.keyId').cacheable(),

  toggleVisibility: function () {
    if (this.get('amVisible')) {
      FLOW.selectedControl.set('selectedQuestion', null);
      FLOW.selectedControl.set('selectedQuestionGroup', null);
    } else {
      FLOW.selectedControl.set('selectedQuestionGroup', this.content);
    }
  },

  doQGroupNameEdit: function () {
    this.set('showQGroupNameEditField', true);
    this.set('showSaveCancelButton', true);
  },

  // fired when 'save' is clicked
  saveQuestionGroup: function () {
    var path, qgId, questionGroup;
    qgId = this.content.get('id');
    questionGroup = FLOW.store.find(FLOW.QuestionGroup, qgId);
    path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name');
    questionGroup.set('code', this.content.get('code'));
    questionGroup.set('name', this.content.get('code'));
    questionGroup.set('path', path);
    questionGroup.set('repeatable', this.content.get('repeatable'));
    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');

    this.set('showQGroupNameEditField', false);
    this.set('showSaveCancelButton', false);

    FLOW.store.commit();
  },

  eventManager: Ember.Object.create({
    click: function(event, clickedView) {
      if (clickedView.type === 'checkbox') {
        var parentView = clickedView.get('parentView');
        parentView.set('showSaveCancelButton', true);
      }
    }
  }),

  // fired when 'cancel' is clicked while showing edit group name field. Cancels the edit.
  cancelQuestionGroupNameEdit: function () {
    this.set('showQGroupNameEditField', false);
    this.set('showSaveCancelButton', false);
  },

  // true if one question group has been selected for Move
  oneSelectedForMove: function () {
    var selectedForMove, selectedSurvey;
    selectedForMove = FLOW.selectedControl.get('selectedForMoveQuestionGroup');
    selectedSurvey = FLOW.selectedControl.get('selectedSurvey');

    if (selectedForMove && selectedSurvey) {
      return selectedForMove.get('surveyId') === selectedSurvey.get('keyId');
    }
  }.property('FLOW.selectedControl.selectedForMoveQuestionGroup'),

  // true if one question group has been selected for Copy
  oneSelectedForCopy: function () {
    var selectedForCopy, selectedSurvey;
    selectedForCopy = FLOW.selectedControl.get('selectedForCopyQuestionGroup');
    selectedSurvey = FLOW.selectedControl.get('selectedSurvey');

    if (selectedForCopy && selectedSurvey) {
      return selectedForCopy.get('surveyId') === selectedSurvey.get('keyId');
    }
  }.property('FLOW.selectedControl.selectedForCopyQuestionGroup'),

  // execute group delete
  deleteQuestionGroup: function () {
    var qgId = this.content.get('id');
    var questionGroup = FLOW.store.find(FLOW.QuestionGroup, qgId);

    // do preflight check if deleting this question group is allowed
    FLOW.store.findQuery(FLOW.QuestionGroup, {
      preflight: 'delete',
      questionGroupId: qgId
    });
  },

  // insert group
  doInsertQuestionGroup: function () {
    var insertAfterOrder, path, sId;
    path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name');
    if (FLOW.selectedControl.selectedSurvey.get('keyId')) {

      if (this.get('zeroItem')) {
        insertAfterOrder = 0;
      } else {
        insertAfterOrder = this.content.get('order');
      }

      // restore order
      sId = FLOW.selectedControl.selectedSurvey.get('keyId');

      // reorder the rest of the question groups
      FLOW.questionGroupControl.reorderQuestionGroups(sId, insertAfterOrder, "increment");

      // create new QuestionGroup item in the store
      FLOW.store.createRecord(FLOW.QuestionGroup, {
        "code": Ember.String.loc('_new_group_please_change_name'),
        "name": Ember.String.loc('_new_group_please_change_name'),
        "order": insertAfterOrder + 1,
        "path": path,
        "status": "READY",
        "surveyId": FLOW.selectedControl.selectedSurvey.get('keyId')
      });

      FLOW.questionGroupControl.submitBulkQuestionGroupsReorder(sId);

      FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
      FLOW.store.commit();
      FLOW.questionGroupControl.setFilteredContent();
    } else {
      FLOW.dialogControl.set('activeAction', "ignore");
      FLOW.dialogControl.set('header', Ember.String.loc('_please_save_survey'));
      FLOW.dialogControl.set('message', Ember.String.loc('_please_save_survey_text'));
      FLOW.dialogControl.set('showCANCEL', false);
      FLOW.dialogControl.set('showDialog', true);
    }
  },

  // prepare for group copy. Shows 'copy to here' buttons
  doQGroupCopy: function () {
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', this.content);
    FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
  },


  // cancel group copy
  doQGroupCopyCancel: function () {
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
  },


  // prepare for group move. Shows 'move here' buttons
  doQGroupMove: function () {
    FLOW.selectedControl.set('selectedForMoveQuestionGroup', this.content);
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
  },

  // cancel group move
  doQGroupMoveCancel: function () {
    FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
  },

  // execute group move to selected location
  doQGroupMoveHere: function () {
    var selectedOrder, insertAfterOrder, selectedQG, sId, questionGroupsInSurvey, origOrder, movingUp;
    selectedOrder = FLOW.selectedControl.selectedForMoveQuestionGroup.get('order');

    if (this.get('zeroItem')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }

    // only do something if we are not moving to the same place
    if (!((selectedOrder == insertAfterOrder) || (selectedOrder == (insertAfterOrder + 1)))) {
      selectedQG = FLOW.store.find(FLOW.QuestionGroup, FLOW.selectedControl.selectedForMoveQuestionGroup.get('keyId'));
      if (selectedQG !== null) {

        // selectedQG.set('order', insertAfterOrder + 1);
        // restore order
        sId = FLOW.selectedControl.selectedSurvey.get('keyId');
        questionGroupsInSurvey = FLOW.store.filter(FLOW.QuestionGroup, function (item) {
          return item.get('surveyId') == sId;
        });

        origOrder = FLOW.selectedControl.selectedForMoveQuestionGroup.get('order');
        movingUp = origOrder < insertAfterOrder;

        questionGroupsInSurvey.forEach(function (item) {
          currentOrder = item.get('order');
          if (movingUp) {
            if (currentOrder == origOrder) {
              // move moving item to right location
              selectedQG.set('order', insertAfterOrder);
            } else if ((currentOrder > origOrder) && (currentOrder <= insertAfterOrder)) {
              // move item down
              item.set('order', item.get('order') - 1);
            }
          } else {
            // Moving down
            if (currentOrder == origOrder) {
              // move moving item to right location
              selectedQG.set('order', insertAfterOrder + 1);
            } else if ((currentOrder < origOrder) && (currentOrder > insertAfterOrder)) {
              // move item up
              item.set('order', item.get('order') + 1);
            }
          }
        });

        FLOW.questionGroupControl.submitBulkQuestionGroupsReorder(sId);

        FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
        FLOW.store.commit();
      }
    }

    FLOW.selectedControl.set('selectedForMoveQuestionGroup', null);
  },

  /*
   *  Request question group and check whether copying is completed on the server side
   *  then load questions for that group.
   */
  ajaxCall: function(qgId){
      var self = this;
      $.ajax({
          url: '/rest/question_groups/' + qgId,
          type: 'GET',
          success: function(data) {
            if (data.question_group.status == "READY") {
                // reload this question group the Ember way, so the UI is updated
                FLOW.questionGroupControl.getQuestionGroup(self.content.get('keyId'));
                // load the questions inside this question group
                FLOW.questionControl.populateQuestionGroupQuestions(self.content.get('keyId'));
            }
          },
          error: function() {
            console.error("Error in checking ready status survey group copy");
          }
      });
  },

  // cycle until our local question group has an id
  // when this is done, start monitoring the status of the remote question group
  pollQuestionGroupStatus: function(){
      var self = this;
      clearInterval(this.pollingTimer);
      if (this.get('amCopying')){
          this.pollingTimer = setInterval(function () {
              // if the question group has a keyId, we can start polling it remotely
              if (self.content && self.content.get('keyId')) {
                  // we have an id and can start polling remotely
                  self.ajaxCall(self.content.get('keyId'));
              }
          },1000);
      }
  }.observes('this.amCopying'),

  // execute group copy to selected location
  doQGroupCopyHere: function () {
    var insertAfterOrder, path, sId;
    path = FLOW.selectedControl.selectedSurveyGroup.get('code') + "/" + FLOW.selectedControl.selectedSurvey.get('name');

    if (this.get('zeroItem')) {
      insertAfterOrder = 0;
    } else {
      insertAfterOrder = this.content.get('order');
    }

    sId = FLOW.selectedControl.selectedSurvey.get('keyId');

    // restore order
    FLOW.questionGroupControl.reorderQuestionGroups(sId, insertAfterOrder, "increment");

    FLOW.store.createRecord(FLOW.QuestionGroup, {
      "order": insertAfterOrder + 1,
      "code": FLOW.selectedControl.selectedForCopyQuestionGroup.get('code'),
      "name": FLOW.selectedControl.selectedForCopyQuestionGroup.get('code'),
      "path": path,
      "status": "COPYING",
      "surveyId": FLOW.selectedControl.selectedForCopyQuestionGroup.get('surveyId'),
      "sourceId":FLOW.selectedControl.selectedForCopyQuestionGroup.get('keyId'),
      "repeatable":FLOW.selectedControl.selectedForCopyQuestionGroup.get('repeatable')
    });

    FLOW.questionGroupControl.submitBulkQuestionGroupsReorder(sId);

    FLOW.selectedControl.selectedSurvey.set('status', 'NOT_PUBLISHED');
    FLOW.store.commit();
    FLOW.selectedControl.set('selectedForCopyQuestionGroup', null);
  },

  showQuestionGroupModifyButtons: function() {
    var form = FLOW.selectedControl.get('selectedSurvey');
    return FLOW.permControl.canEditForm(form);
  }.property('FLOW.selectedControl.selectedSurvey'),

  disableQuestionGroupEditing: function() {
    var form = FLOW.selectedControl.get('selectedSurvey');
    return !FLOW.permControl.canEditForm(form);
  }.property('FLOW.selectedControl.selectedSurvey'),
});

});

loader.register('akvo-flow/views/surveys/survey-group-views', function(require) {
function capitaliseFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

if (!String.prototype.trim) {
  String.prototype.trim = function(){
    return this.replace(/^\s+|\s+$/g, '');
  };
}

FLOW.ProjectListView = FLOW.View.extend({
  templateName: 'navSurveys/project-list'
});

FLOW.ProjectView = FLOW.View.extend({
  templateName: 'navSurveys/project',
});

FLOW.Project = FLOW.View.extend({

  showProjectDetails: false,
  showAdvancedSettings: false,
  selectedLanguage: null,
  monitoringGroupEnabled: false,
  currentRegistrationForm: null,
  showDataApprovalDetails: false,

  /* computer property for setting / getting the value of the current
  registration form */
  selectedRegistrationForm: function(key, value, previousValue){
    var registrationForm, formId;

    if(arguments.length > 1) {
        this.set('currentRegistrationForm', value);
    }

    registrationForm = this.get('currentRegistrationForm');
    if(!registrationForm) {
        formId = FLOW.projectControl.currentProject.get('newLocaleSurveyId');
        registrationForm = FLOW.surveyControl.content.filter(function(item){
                return item.get('keyId') === formId;
        })[0];
        this.set('currentRegistrationForm', registrationForm);
    }
    return registrationForm;
  }.property('FLOW.projectControl.currentProject'),

  /*
   * property for setting the currently selected approval group
   */
  selectedApprovalGroup: function () {
      var approvalGroupId = FLOW.projectControl.currentProject.get('dataApprovalGroupId');
      var approvalGroupList = FLOW.router.approvalGroupListController.get('content');
      var approvalGroup = approvalGroupList &&
                              approvalGroupList.filterProperty('keyId', approvalGroupId).get('firstObject');
      return approvalGroup;
  }.property('FLOW.projectControl.currentProject'),

  project: function() {
    return FLOW.projectControl.get('currentProject');
  }.property(),

  toggleShowProjectDetails: function() {
    this.set('showProjectDetails', !this.get('showProjectDetails'));
  },

  /*
   * Toggle advanced settings and load data approval
   * groups if data approval is enabled on the instance
   */
  toggleShowAdvancedSettings: function() {
      var approvalGroupListController = FLOW.router.get('approvalGroupListController');
      if(FLOW.Env.enableDataApproval && !approvalGroupListController.content) {
          var self = this;

          var groups = FLOW.ApprovalGroup.find({});
          approvalGroupListController.set('content', groups);

          // only toggle the property after approval groups are retrieved
          groups.on('didLoad', function () {
              self.toggleProperty('showAdvancedSettings');
          });
      } else {
          this.toggleProperty('showAdvancedSettings');
      }
  },

  isNewProject: function() {
    var currentProject = FLOW.projectControl.get('currentProject');
    return currentProject && currentProject.get('code') == "New survey";
  }.property('FLOW.projectControl.currentProject'),

  visibleProjectBasics: function() {
    return this.get('isNewProject') || this.get('showProjectDetails');
  }.property('showProjectDetails'),

  updateSelectedLanguage: function() {
    var currentProject = FLOW.projectControl.get('currentProject');
    if (currentProject)
      currentProject.set('defaultLanguageCode', this.selectedLanguage.get('value'));
  }.observes('this.selectedLanguage'),

  showMonitoringGroupCheckbox: function() {
    return FLOW.projectControl.get('formCount') < 2;
  }.property("FLOW.projectControl.formCount"),

  updateSelectedRegistrationForm: function() {
    if (!this.get('currentRegistrationForm')) return;
    FLOW.projectControl.currentProject.set('newLocaleSurveyId', this.currentRegistrationForm.get('keyId'));
  }.observes('currentRegistrationForm'),

  isPublished: function() {
    var form;
    if (!Ember.none(FLOW.selectedControl.get('selectedSurvey'))) {
      form = FLOW.selectedControl.get('selectedSurvey');
    } else {
      if (FLOW.surveyControl.content.get('isLoaded')) {
        form = FLOW.surveyControl.content.get('firstObject');
        FLOW.selectedControl.set('selectedSurvey', form);
      }
    }
    return form.get('status') === 'PUBLISHED'
  }.property('FLOW.selectedControl.selectedSurvey.status'),

  disableFolderSurveyInputField: function() {
    var permissions = FLOW.projectControl.get('currentFolderPermissions');
    return permissions.indexOf("PROJECT_FOLDER_UPDATE") < 0;
  }.property('FLOW.projectControl.currentProjectPath'),

  showAddNewFormButton: function () {
    var survey = FLOW.projectControl.get('currentProject');
    return FLOW.permControl.canEditSurvey(survey);
  }.property(),

  showDataApproval: function () {
      return FLOW.Env.enableDataApproval;
  }.property(),

  showDataApprovalList: function () {
      return FLOW.projectControl.currentProject.get('requireDataApproval');
  }.property('FLOW.projectControl.currentProject.requireDataApproval'),

  toggleShowDataApprovalDetails: function () {
      this.set('showDataApprovalDetails', !this.get('showDataApprovalDetails'));
  },
});

FLOW.SurveyApprovalView = FLOW.View.extend({});

FLOW.SurveyApprovalStepView = FLOW.View.extend({
    step: null,

    showResponsibleUsers: false,


    toggleShowResponsibleUsers: function () {
        this.toggleProperty('showResponsibleUsers');
        this.loadUsers();
    },

    /*
     * load the users list if not present
     */
    loadUsers: function() {
        var users = FLOW.router.userListController.get('content');
        if(Ember.empty(users)) {
            FLOW.router.userListController.set('content', FLOW.User.find());
        }
    },
});

FLOW.ApprovalResponsibleUserView = FLOW.View.extend({
    user: null,

    step: null,

    isResponsibleUser: function (key, isCheckedValue, previousCheckedValue) {
        var step = this.get('step');
        var user = this.get('user');

        if (!step || !user) {
            return false;
        }

        // create a new list to force enabling of 'Save' button for surveys
        // when a user is added or removed from approver list
        var approverUserList = Ember.A();
        if(!Ember.empty(step.get('approverUserList'))) {
            approverUserList.pushObjects(step.get('approverUserList'));
        }

        // setter
        if(arguments.length > 1) {
            if (isCheckedValue) {
                approverUserList.addObject(user.get('keyId'));
            } else {
                approverUserList.removeObject(user.get('keyId'));
            }
            step.set('approverUserList', approverUserList);
        }

        // getter
        return approverUserList.contains(user.get('keyId'));
    }.property('this.step.approverUserList'),
});

FLOW.ProjectMainView = FLOW.View.extend({
  hasUnsavedChanges: function() {
    var selectedProject = FLOW.projectControl.get('currentProject');
    var isProjectDirty = selectedProject ? selectedProject.get('isDirty') : false;

    var selectedForm = FLOW.selectedControl.get('selectedSurvey');
    var isFormDirty = selectedForm ? selectedForm.get('isDirty') : false;

    var approvalSteps = FLOW.router.approvalStepsController.get('content');
    var isApprovalStepDirty = false;

    if (approvalSteps) {
        approvalSteps.forEach(function (step) {
            if (!isApprovalStepDirty && step.get('isDirty')) {
                isApprovalStepDirty = true;
            }
        });
    }

    return isProjectDirty || isFormDirty || isApprovalStepDirty;

  }.property('FLOW.projectControl.currentProject.isDirty',
              'FLOW.selectedControl.selectedSurvey.isDirty',
              'FLOW.router.approvalStepsController.content.@each.approverUserList'),

  projectView: function() {
    return FLOW.projectControl.isProject(FLOW.projectControl.get('currentProject'));
  }.property('FLOW.projectControl.currentProject'),

  projectListView: function() {
    return FLOW.projectControl.isProjectFolder(FLOW.projectControl.get('currentProject'));
  }.property('FLOW.projectControl.currentProject'),

  disableAddFolderButton: function() {
    var permissions = FLOW.projectControl.get('currentFolderPermissions');
    return permissions.indexOf("PROJECT_FOLDER_CREATE") < 0;
  }.property('FLOW.projectControl.currentProjectPath'),

  disableAddSurveyButtonInRoot: function() {
    return FLOW.projectControl.get('currentProjectPath').length == 0;
  }.property('FLOW.projectControl.currentProjectPath'),

  disableAddSurveyButton: function() {
    var permissions = FLOW.projectControl.get('currentFolderPermissions');
    return permissions.indexOf("PROJECT_FOLDER_CREATE") < 0;
  }.property('FLOW.projectControl.currentProjectPath'),
});


FLOW.ProjectList = FLOW.View.extend({
  tagName: 'ul',
  classNameBindings: ['classProperty'],
  classProperty: function() {
    return FLOW.projectControl.moveTarget || FLOW.projectControl.copyTarget ? 'actionProcess' : '';
  }.property('FLOW.projectControl.moveTarget', 'FLOW.projectControl.copyTarget')
});

FLOW.ProjectItemView = FLOW.View.extend({
  tagName: 'li',
  content: null,
  classNameBindings: ['classProperty'],
  folderEdit: false,

  classProperty: function() {
    var isFolder = FLOW.projectControl.isProjectFolder(this.content);
    var isFolderEmpty = FLOW.projectControl.isProjectFolderEmpty(this.content);
    var isMoving = this.content === FLOW.projectControl.get('moveTarget');
    var isCopying = this.content === FLOW.projectControl.get('copyTarget');

    var classes = "aSurvey";
    if (isFolder) classes += " aFolder";
    if (isFolderEmpty) classes += " folderEmpty";
    if (isMoving || isCopying) classes += " highLighted";
    if (FLOW.projectControl.get('newlyCreated') === this.get('content')) classes += " newlyCreated";

    return classes;
  }.property('FLOW.projectControl.moveTarget', 'FLOW.projectControl.copyTarget', 'FLOW.projectControl.currentProject'),

  toggleEditFolderName: function(evt) {
    this.set('folderEdit', !this.get('folderEdit'));
  },

  isFolder: function() {
    return FLOW.projectControl.isProjectFolder(this.content);
  }.property(),

  formatDate: function(datetime) {
    if (datetime === "") return "";
    var date = new Date(parseInt(datetime, 10));
    return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
  },

  created: function() {
    return this.formatDate(this.content.get('createdDateTime'));
  }.property('this.content.createdDateTime'),

  modified: function() {
    return this.formatDate(this.content.get('lastUpdateDateTime'));
  }.property('this.content.lastUpdateDateTime'),

  isPrivate: function() {
    return this.content.get('privacyLevel') === "PRIVATE";
  }.property(),

  language: function() {
    var langs = {en: "English", es: "Español", fr: "Français"};
    return langs[this.content.get('defaultLanguageCode')];
  }.property(),

  hideFolderSurveyDeleteButton: function () {
    var c = this.get('content');
    var permissions = FLOW.projectControl.get('currentFolderPermissions');
    return permissions.indexOf("PROJECT_FOLDER_DELETE") < 0 || !Ember.empty(c.get('surveyList'));
  }.property(),

  showSurveyEditButton: function() {
    var survey = this.get('content');
    return FLOW.permControl.canEditSurvey(survey);
  }.property(),

  showSurveyMoveButton: function() {
    var survey = this.get('content');
    return FLOW.permControl.canEditSurvey(survey);
  }.property(),

  showSurveyCopyButton: function () {
    var survey = this.get('content');
    return FLOW.permControl.canEditSurvey(survey);
  }.property()
});

FLOW.FolderEditView = Ember.TextField.extend({
  content: null,
  path: null,

  saveFolderName: function() {
    var name = this.content.get('code').trim();
    this.content.set('name', name);
    this.content.set('code', name);
    var path = FLOW.projectControl.get('currentProjectPath') + "/" + name;
    this.content.set('path', path);
    FLOW.store.commit();
  },

  focusOut: function() {
    this.get('parentView').set('folderEdit', false);
    this.saveFolderName();
  },

  insertNewline: function() {
    this.get('parentView').set('folderEdit', false);
  }
});

FLOW.FormTabView = Ember.View.extend({
  tagName: 'li',
  content: null,
  classNameBindings: ['classProperty'],

  classProperty: function() {

    var form = this.get('content');
    var currentProject = FLOW.projectControl.get('currentProject');
    var classString = 'aFormTab';

    if (form === null || currentProject === null) return classString;

    // Return "aFormTab" "current" and/or "registrationForm"
    var isActive = form === FLOW.selectedControl.get('selectedSurvey');
    var isRegistrationForm = currentProject.get('monitoringGroup') && form.get('keyId') === currentProject.get('newLocaleSurveyId');
    var isPublished = form.get('status') === 'PUBLISHED';

    if (isActive) classString += ' current';
    if (isRegistrationForm) classString += ' registrationForm';
    if (isPublished) classString += ' published'

    return classString;
  }.property('FLOW.selectedControl.selectedSurvey', 'FLOW.projectControl.currentProject.newLocaleSurveyId', 'content.status' ),
});

});

loader.register('akvo-flow/views/surveys/translations-view', function(require) {
FLOW.TranslationsView = FLOW.View.extend({
  templateName: 'navSurveys/manage-translations',

  saveTranslationsAndClose: function () {
    FLOW.translationControl.saveTranslations();
    this.get('parentView').set('manageTranslations', false);
  },

  closeTranslations: function (router, event) {
    this.get('parentView').set('manageTranslations', false);
  },
});

});

loader.register('akvo-flow/views/users/user-view', function(require) {
FLOW.UserListView = FLOW.View.extend({
  showAddUserBool: false,
  showEditUserBool: false,
  showManageApiKeysBool: false,

  showAddUserDialog: function () {
    var userPerm;
    FLOW.editControl.set('newUserName', null);
    FLOW.editControl.set('newEmailAddress', null);

    userPerm = FLOW.permissionLevelControl.find(function (item) {
      return item.value == 20; // USER
    });
    FLOW.editControl.set('newPermissionLevel', userPerm);

    this.set('showAddUserBool', true);
  },

  doAddUser: function () {
    var value = null,
      superAdmin = false;
    if (FLOW.editControl.newPermissionLevel !== null) {
      value = FLOW.editControl.newPermissionLevel.value;
    } else {
      value = null;
    }

    if (value === 0) {
      value = 20; // Can't create a Super Admin from UI
      superAdmin = true;
    }

    FLOW.store.createRecord(FLOW.User, {
      "userName": FLOW.editControl.get('newUserName'),
      "emailAddress": Ember.$.trim(FLOW.editControl.get('newEmailAddress').toLowerCase()),
      "permissionList": value
    });

    FLOW.store.commit();
    this.set('showAddUserBool', false);

    if (superAdmin) {
      this.showRoleWarning();
    }

  },

  cancelAddUser: function () {
    this.set('showAddUserBool', false);
  },

  showEditUserDialog: function (event) {
    var permission = null;
    FLOW.editControl.set('editUserName', event.context.get('userName'));
    FLOW.editControl.set('editEmailAddress', event.context.get('emailAddress'));
    FLOW.editControl.set('editUserId', event.context.get('keyId'));

    permission = FLOW.permissionLevelControl.find(function (item) {
      return item.value == event.context.get('permissionList');
    });

    FLOW.editControl.set('editPermissionLevel', permission);
    this.set('showEditUserBool', true);
  },

  doEditUser: function () {
    var user, superAdmin = false;
    user = FLOW.store.find(FLOW.User, FLOW.editControl.get('editUserId'));
    user.set('userName', FLOW.editControl.get('editUserName'));
    user.set('emailAddress', Ember.$.trim(FLOW.editControl.get('editEmailAddress').toLowerCase()));

    if (FLOW.editControl.editPermissionLevel !== null) {
      if (FLOW.editControl.editPermissionLevel.value === 0) {
        superAdmin = true;
        user.set('permissionList', 20); // Can't change to Super Admin
      } else {
        user.set('permissionList', FLOW.editControl.editPermissionLevel.value);
      }
    }

    FLOW.store.commit();
    this.set('showEditUserBool', false);

    if (superAdmin) {
      this.showRoleWarning();
    }
  },

  cancelEditUser: function () {
    this.set('showEditUserBool', false);
  },

  showRoleWarning: function () {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_manage_users_and_user_rights'));
    FLOW.dialogControl.set('message', Ember.String.loc('_cant_set_superadmin'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },

  showManageApiKeysDialog: function (event) {
    FLOW.editControl.set('manageAccessKey', event.context.get('accessKey'));
    FLOW.editControl.set('showSecret', false);
    FLOW.editControl.set('manageApiUserId', event.context.get('keyId'));
    this.set('showManageApiKeysBool', true);
  },

  doGenerateNewApiKey: function (event) {

    var userId = FLOW.editControl.get('manageApiUserId');

    $.ajax({
      url: '/rest/users/' + userId + '/apikeys',
      type: 'POST',
      success: function(data) {
        var user = FLOW.store.find(FLOW.User, userId);
        var accessKey = data.apikeys.accessKey;
        var secret = data.apikeys.secret;

        user.set('accessKey', accessKey);

        FLOW.editControl.set('manageAccessKey', accessKey);
        FLOW.editControl.set('manageSecret', secret);
        FLOW.editControl.set('showSecret', true);
      },
      error: function() {
        console.error('Could not create apikeys');
      }
    });
  },

  doRevokeApiKey: function(event) {

    var userId = FLOW.editControl.get('manageApiUserId');

    $.ajax({
      url: '/rest/users/' + userId + '/apikeys',
      type: 'DELETE',
      success: function(data) {
        var user = FLOW.store.find(FLOW.User, userId);
        user.set('accessKey', null);

        FLOW.editControl.set('manageAccessKey', null);
        FLOW.editControl.set('manageSecret', null);
        FLOW.editControl.set('showSecret', false);
      },
      error: function() {
        console.error('Could not delete apikeys.')
      }
    });
  },

  cancelManageApiKeys: function() {
    this.set('showManageApiKeysBool', false);
  }
});

FLOW.UserView = FLOW.View.extend({
  tagName: 'span',
  deleteUser: function () {
    var user;
    user = FLOW.store.find(FLOW.User, this.content.get('keyId'));
    if (user !== null) {
      user.deleteRecord();
      FLOW.store.commit();
    }
  }
});

FLOW.SingleUserView = FLOW.View.extend({
  tagName: 'td',
  permissionLevel: null,
  roleLabel: null,

  init: function () {
    var role = null;
    this._super();

    role = FLOW.permissionLevelControl.find(function (item) {
      return item.value == this.content.get('permissionList');
    }, this);


    if (Ember.none(role)) {
      this.set('roleLabel', Ember.String.loc('_please_reset_the_role_for_this_user'));
      this.set('roleClass', 'notFound');
    } else {
      this.set('roleLabel', role.label);
      this.set('roleClass', Ember.String.camelize(role.label));
    }
  }
});

});

loader.register('akvo-flow/views/views-public', function(require) {
// ***********************************************//
//                      Navigation views
// ***********************************************//
/*global tooltip, makePlaceholders */

require('akvo-flow/core-common');
require('akvo-flow/views/maps/map-views-common-public');


FLOW.ApplicationView = Ember.View.extend({
  templateName: 'application/application-public',
});


FLOW.locale = function (i18nKey) {
  return 'Ember.STRINGS._select_survey_group';
  // var i18nValue;
  // try {
  //   i18nValue = Ember.String.loc(i18nKey);
  // }
  // catch (err) {
  //   return i18nKey;
  // }
  // return i18nValue;
};

// ***********************************************//
//                      Handlebar helpers
// ***********************************************//
// localisation helper
Ember.Handlebars.registerHelper('t', function (i18nKey, options) {
  var i18nValue;
  try {
    i18nValue = Ember.String.loc(i18nKey);
  } catch (err) {
    return i18nKey;
  }
  return i18nValue;
});


Ember.Handlebars.registerHelper('tooltip', function (i18nKey) {
  var tooltip;
  try {
    tooltip = Ember.String.loc(i18nKey);
  } catch (err) {
    tooltip = i18nKey;
  }
  tooltip = Handlebars.Utils.escapeExpression(tooltip);
  return new Handlebars.SafeString(
    '<a href="#" class="helpIcon tooltip" title="' + tooltip + '">?</a>'
  );
});

FLOW.renderCaddisflyAnswer = function(json){
  var name = ""
  var imageUrl = ""
  var result = Ember.A();
  if (!Ember.empty(json)){
      try {
          var jsonParsed = JSON.parse(json);

          // get out image url
          if (!Ember.empty(jsonParsed.image)){
            imageUrl = FLOW.Env.photo_url_root + jsonParsed.image.trim();
          }

          // contruct html
          html = "<div><strong>" + name + "</strong></div>"
          html += jsonParsed.result.map(function(item){
                  return "<br><div>" + item.name + " : " + item.value + " " + item.unit + "</div>";
              }).join("\n");
          html += "<br>"
          html += "<div class=\"signatureImage\"><img src=\"" + imageUrl +"\"}} /></div>"
          return html;
      } catch (e) {
          return json;
      }
  } else {
    return "Wrong JSON format";
  }
}

Ember.Handlebars.registerHelper('placemarkDetail', function () {
  var answer, markup, question, cascadeJson, optionJson, cascadeString = "",
  questionType, imageSrcAttr, signatureJson, photoJson;

  question = Ember.get(this, 'questionText');
  answer = Ember.get(this, 'stringValue') || '';
  answer = answer.replace(/\|/g, ' | '); // geo, option and cascade data
  answer = answer.replace(/\//g, ' / '); // also split folder paths
  questionType = Ember.get(this, 'questionType');

  if (questionType === 'CASCADE') {

      if (answer.indexOf("|") > -1) {
        // ignore
      } else {
        cascadeJson = JSON.parse(answer);
        answer = cascadeJson.map(function(item){
          return item.name;
        }).join("|");
      }
  } else if ((questionType === 'VIDEO' || questionType === 'PHOTO') && answer.charAt(0) === '{') {
    photoJson = JSON.parse(answer)
    var mediaAnswer = photoJson.filename;

    var mediaFileURL = FLOW.Env.photo_url_root + mediaAnswer.split('/').pop().replace(/\s/g, '');
    if (questionType == "PHOTO") {
        answer = '<div class=":imgContainer photoUrl:shown:hidden">'
        +'<a class="media" href="'+mediaFileURL+'" target="_blank"><img src="'+mediaFileURL+'" alt=""/></a>'
        +'</div>';
    } else if (questionType == "VIDEO") {
        answer = '<div><div class="media">'+mediaFileURL+'</div><br>'
        +'<a href="'+mediaFileURL+'" target="_blank">'+Ember.String.loc('_open_video')+'</a>'
        +'</div>';
    }
  } else if (questionType === 'OPTION' && answer.charAt(0) === '[') {
    optionJson = JSON.parse(answer);
    answer = optionJson.map(function(item){
      return item.text;
    }).join("|");
  } else if (questionType === 'SIGNATURE') {
    imageSrcAttr = 'data:image/png;base64,';
    signatureJson = JSON.parse(answer);
    answer = signatureJson && imageSrcAttr + signatureJson.image || '';
    answer = answer && '<img src="' + answer + '" />';
    answer = answer && answer + '<div>' + Ember.String.loc('_signed_by') + ':' + signatureJson.name + '</div>' || '';
  } else if (questionType === 'DATE') {
    answer = renderTimeStamp(answer);
  } else if (questionType === 'CADDISFLY'){
    answer = FLOW.renderCaddisflyAnswer(answer)
  }

  markup = '<div class="defListWrap"><dt>' +
    question + ':</dt><dd>' +
    answer + '</dd></div>';

  return new Handlebars.SafeString(markup);
});

/*  Take a timestamp and render it as a date in format
    YYYY-mm-dd */
function renderTimeStamp(timestamp) {
  var d, t, date, month, year;
  t = parseInt(timestamp, 10);
  if (isNaN(t)) {
    return "";
  }

  d = new Date(t);
  if (!d){
	  return "";
  }
  date = d.getDate();
  month = d.getMonth() + 1;
  year = d.getFullYear();

  if (month < 10) {
    monthString = "0" + month.toString();
  } else {
    monthString = month.toString();
  }

  if (date < 10) {
    dateString = "0" + date.toString();
  } else {
    dateString = date.toString();
  }

  return year + "-" + monthString + "-" + dateString;
}

// translates values to labels for languages
Ember.Handlebars.registerHelper('toLanguage', function (value) {
  var label, valueLoc;
  label = "";
  valueLoc = Ember.get(this, value);

  FLOW.languageControl.get('content').forEach(function (item) {
    if (item.get('value') == valueLoc) {
      label = item.get('label');
    }
  });
  return label;
});

// add space to vertical bar helper
Ember.Handlebars.registerHelper('addSpace', function (property) {
  return Ember.get(this, property).replace(/\|/g, ' | ');
});

Ember.Handlebars.registerHelper("getServer", function () {
  var loc = window.location.href,
    pos = loc.indexOf("/admin");
  return loc.substring(0, pos);
});

// Register a Handlebars helper that instantiates `view`.
// The view will have its `content` property bound to the
// helper argument.
FLOW.registerViewHelper = function (name, view) {
  Ember.Handlebars.registerHelper(name, function (property, options) {
    options.hash.contentBinding = property;
    return Ember.Handlebars.helpers.view.call(this, view, options);
  });
};


FLOW.registerViewHelper('date2', Ember.View.extend({
  tagName: 'span',

  template: Ember.Handlebars.compile('{{view.formattedContent}}'),

  formattedContent: (function () {
    var content, d, curr_date, curr_month, curr_year, curr_hour, curr_min, monthString, dateString, hourString, minString;
    content = this.get('content');

    if (content === null) {
      return "";
    }

    d = new Date(parseInt(content, 10));
    curr_date = d.getDate();
    curr_month = d.getMonth() + 1;
    curr_year = d.getFullYear();
    curr_hour = d.getHours();
    curr_min = d.getMinutes();

    if (curr_month < 10) {
      monthString = "0" + curr_month.toString();
    } else {
      monthString = curr_month.toString();
    }

    if (curr_date < 10) {
      dateString = "0" + curr_date.toString();
    } else {
      dateString = curr_date.toString();
    }

    if (curr_hour < 10) {
      hourString = "0" + curr_hour.toString();
    } else {
      hourString = curr_hour.toString();
    }

    if (curr_min < 10) {
      minString = "0" + curr_min.toString();
    } else {
      minString = curr_min.toString();
    }

    return curr_year + "-" + monthString + "-" + dateString + "  " + hourString + ":" + minString;
  }).property('content')
}));


// ********************************************************//
//                      standard views
// ********************************************************//
// TODO check if doing this in View is not impacting performance,
// as some pages have a lot of views (all navigation elements, for example)
// one way could be use an extended copy of view, with the didInsertElement,
// for some of the elements, and not for others.
Ember.View.reopen({
  didInsertElement: function () {
    this._super();
    tooltip();
  }
});

Ember.Select.reopen({
  attributeBindings: ['size']
});

FLOW.FooterView = FLOW.View.extend({
  templateName: 'application/footer-public'
});

});

loader.register('akvo-flow/views/views', function(require) {
// ***********************************************//
//                      Navigation views
// ***********************************************//
/*global tooltip, makePlaceholders */

require('akvo-flow/core-common');
require('akvo-flow/views/surveys/preview-view');
require('akvo-flow/views/surveys/notifications-view');
require('akvo-flow/views/surveys/translations-view');
require('akvo-flow/views/surveys/survey-group-views');
require('akvo-flow/views/surveys/survey-details-views');
require('akvo-flow/views/surveys/form-view');
require('akvo-flow/views/data/inspect-data-table-views');
require('akvo-flow/views/data/bulk-upload-view');
require('akvo-flow/views/data/monitoring-data-table-view');
require('akvo-flow/views/data/cascade-resources-view');
require('akvo-flow/views/data/data-approval-views');
require('akvo-flow/views/surveys/question-view');
require('akvo-flow/views/data/question-answer-view');
require('akvo-flow/views/reports/report-views');
require('akvo-flow/views/reports/export-reports-views');
require('akvo-flow/views/maps/map-views-common');
require('akvo-flow/views/messages/message-view');
require('akvo-flow/views/devices/devices-views');
require('akvo-flow/views/devices/assignments-list-tab-view');
require('akvo-flow/views/devices/assignment-edit-views');
require('akvo-flow/views/devices/survey-bootstrap-view');
require('akvo-flow/views/users/user-view');

FLOW.ApplicationView = Ember.View.extend({
  templateName: 'application/application',
});


FLOW.locale = function (i18nKey) {
  return 'Ember.STRINGS._select_survey_group';
  // var i18nValue;
  // try {
  //   i18nValue = Ember.String.loc(i18nKey);
  // }
  // catch (err) {
  //   return i18nKey;
  // }
  // return i18nValue;
};

// ***********************************************//
//                      Handlebar helpers
// ***********************************************//
// localisation helper
Ember.Handlebars.registerHelper('t', function (i18nKey, options) {
  var i18nValue;
  try {
    i18nValue = Ember.String.loc(i18nKey);
  } catch (err) {
    return i18nKey;
  }
  return i18nValue;
});

Ember.Handlebars.registerHelper('newLines', function (text) {
  var answer = "";
  if (!Ember.none(Ember.get(this, text))) {
    answer = Ember.get(this, text).replace(/\n/g, '<br/>');
  }
  return new Handlebars.SafeString(answer);
});


Ember.Handlebars.registerHelper('if_blank', function (item) {
  var text;
  text = Ember.get(this, item);
  return (text && text.replace(/\s/g, "").length) ? new Handlebars.SafeString('') : new Handlebars.SafeString('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
});

Ember.Handlebars.registerHelper('tooltip', function (i18nKey) {
  var tooltip;
  try {
    tooltip = Ember.String.loc(i18nKey);
  } catch (err) {
    tooltip = i18nKey;
  }
  tooltip = Handlebars.Utils.escapeExpression(tooltip);
  return new Handlebars.SafeString(
    '<a href="#" class="helpIcon tooltip" title="' + tooltip + '">?</a>'
  );
});


FLOW.renderCaddisflyAnswer = function(json){
  var name = ""
  var imageUrl = ""
  var result = Ember.A();
  if (!Ember.empty(json)){
    try {
        var jsonParsed = JSON.parse(json);

        // get out image url
        if (!Ember.empty(jsonParsed.image)){
          imageUrl = FLOW.Env.photo_url_root + jsonParsed.image.trim();
        }

        // contruct html
        html = "<div><strong>" + name + "</strong></div>"
        html += jsonParsed.result.map(function(item){
                return "<br><div>" + item.name + " : " + item.value + " " + item.unit + "</div>";
            }).join("\n");
        html += "<br>"
        html += "<div class=\"signatureImage\"><img src=\"" + imageUrl +"\"}} /></div>"
        return html;
    } catch (e) {
        return json;
    }
  } else {
    return "Wrong JSON format";
  }
}

Ember.Handlebars.registerHelper('placemarkDetail', function () {
  var answer, markup, question, cascadeJson, optionJson, cascadeString = "",
  questionType, imageSrcAttr, signatureJson, photoJson, cartoQuestionType, self=this;

  if (FLOW.Env.mapsProvider === 'cartodb') {
      FLOW.router.mapsController.questions.forEach(function(qItem){
          if (qItem.get("keyId") == Ember.get(self, 'questionID')) {
              cartoQuestionType = qItem.get("type");
          }
      });
  }

  question = Ember.get(this, 'questionText');
  answer = Ember.get(this, FLOW.Env.mapsProvider === 'cartodb' ? 'value': 'stringValue') || '';
  answer = answer.replace(/\|/g, ' | '); // geo, option and cascade data
  answer = answer.replace(/\//g, ' / '); // also split folder paths
  questionType = FLOW.Env.mapsProvider === 'cartodb' ? cartoQuestionType: Ember.get(this, 'questionType');

  if (questionType === 'CASCADE') {

      if (answer.indexOf("|") > -1) {
        // ignore
      } else {
          if (answer.charAt(0) === '[') {
              cascadeJson = JSON.parse(answer);
              answer = cascadeJson.map(function(item){
                return item.name;
              }).join("|");
          }
      }
  } else if ((questionType === 'VIDEO' || questionType === 'PHOTO') && answer.charAt(0) === '{') {
    photoJson = JSON.parse(answer)
    var mediaAnswer = photoJson.filename;

    var mediaFileURL = FLOW.Env.photo_url_root + mediaAnswer.split('/').pop().replace(/\s/g, '');
    if (questionType == "PHOTO") {
        answer = '<div class=":imgContainer photoUrl:shown:hidden">'
        +'<a class="media" data-coordinates=\''
        +((photoJson.location) ? answer : '' )+'\' href="'
        +mediaFileURL+'" target="_blank"><img src="'+mediaFileURL+'" alt=""/></a><br>'
        +((photoJson.location) ? '<a class="media-location" data-coordinates=\''+answer+'\'>'+Ember.String.loc('_show_photo_on_map')+'</a>' : '')
        +'</div>';
    } else if (questionType == "VIDEO") {
        answer = '<div><div class="media" data-coordinates=\''
        +((photoJson.location) ? answer : '' )+'\'>'+mediaFileURL+'</div><br>'
        +'<a href="'+mediaFileURL+'" target="_blank">'+Ember.String.loc('_open_video')+'</a>'
        +((photoJson.location) ? '&nbsp;|&nbsp;<a class="media-location" data-coordinates=\''+answer+'\'>'+Ember.String.loc('_show_photo_on_map')+'</a>' : '')
        +'</div>';
    }
  } else if (questionType === 'OPTION' && answer.charAt(0) === '[') {
    optionJson = JSON.parse(answer);
    answer = optionJson.map(function(item){
      return item.text;
    }).join("|");
  } else if (questionType === 'SIGNATURE') {
    imageSrcAttr = 'data:image/png;base64,';
    signatureJson = JSON.parse(answer);
    answer = signatureJson && imageSrcAttr + signatureJson.image || '';
    answer = answer && '<img src="' + answer + '" />';
    answer = answer && answer + '<div>' + Ember.String.loc('_signed_by') + ':' + signatureJson.name + '</div>' || '';
  } else if (questionType === 'DATE') {
    answer = renderTimeStamp(answer);
  } else if (questionType === 'CADDISFLY'){
    answer = FLOW.renderCaddisflyAnswer(answer)
  } else if (questionType === 'GEOSHAPE') {
    var geoshapeObject = FLOW.parseJSON(answer, "features");
    if (geoshapeObject) {
        answer = '<div class="geoshape-map" data-geoshape-object=\''+answer+'\' style="width:100%; height: 100px; float: left"></div>'
        +'<a style="float: left" class="project-geoshape" data-geoshape-object=\''+answer+'\'>'+Ember.String.loc('_project_onto_main_map')+'</a>';

        if (geoshapeObject['features'][0]['geometry']['type'] === "Polygon"
            || geoshapeObject['features'][0]['geometry']['type'] === "LineString"
                || geoshapeObject['features'][0]['geometry']['type'] === "MultiPoint") {
            answer += '<div style="float: left; width: 100%">'+ Ember.String.loc('_points') +': '+geoshapeObject['features'][0]['properties']['pointCount']+'</div>';
        }

        if (geoshapeObject['features'][0]['geometry']['type'] === "Polygon"
            || geoshapeObject['features'][0]['geometry']['type'] === "LineString") {
            answer += '<div style="float: left; width: 100%">'+ Ember.String.loc('_length') +': '+geoshapeObject['features'][0]['properties']['length']+'m</div>';
        }

        if (geoshapeObject['features'][0]['geometry']['type'] === "Polygon") {
            answer += '<div style="float: left; width: 100%">'+ Ember.String.loc('_area') +': '+geoshapeObject['features'][0]['properties']['area']+'m&sup2;</div>';
        }
    }
  }

  markup = '<div class="defListWrap"><h4>' +
    question + ':</h4><div>' +
    answer + '</div></div>';

  return new Handlebars.SafeString(markup);
});

//if there's geoshape, draw it
Ember.Handlebars.registerHelper('drawGeoshapes', function () {
    var cartoQuestionType, questionType, self=this;
    if (FLOW.Env.mapsProvider === 'cartodb') {
        FLOW.router.mapsController.questions.forEach(function(qItem){
            if (qItem.get("keyId") == Ember.get(self, 'questionID')) {
                cartoQuestionType = qItem.get("type");
            }
        });
    }
    questionType = FLOW.Env.mapsProvider === 'cartodb' ? cartoQuestionType: Ember.get(this, 'questionType');
    if (questionType == "GEOSHAPE") {
        setTimeout(function(){
            $('.geoshape-map').each(function(index){
                FLOW.drawGeoShape($('.geoshape-map')[index], $(this).data('geoshape-object'));
            });
        }, 500);
    }
});

/*  Take a timestamp and render it as a date in format
    YYYY-mm-dd */
function renderTimeStamp(timestamp) {
  var d, t, date, month, year;
  t = parseInt(timestamp, 10);
  if (isNaN(t)) {
  return "";
  }

  d = new Date(t);
  if (!d){
    return "";
  }

  date = d.getDate();
  month = d.getMonth() + 1;
  year = d.getFullYear();

  if (month < 10) {
    monthString = "0" + month.toString();
  } else {
    monthString = month.toString();
  }

  if (date < 10) {
    dateString = "0" + date.toString();
  } else {
    dateString = date.toString();
  }

  return year + "-" + monthString + "-" + dateString;
}

// translates values to labels for languages
Ember.Handlebars.registerHelper('toLanguage', function (value) {
  var label, valueLoc;
  label = "";
  valueLoc = Ember.get(this, value);

  FLOW.languageControl.get('content').forEach(function (item) {
    if (item.get('value') == valueLoc) {
      label = item.get('label');
    }
  });
  return label;
});

// translates values to labels for surveyPointTypes
Ember.Handlebars.registerHelper('toPointType', function (value) {
  var label, valueLoc;
  label = "";
  valueLoc = Ember.get(this, value);

  FLOW.surveyPointTypeControl.get('content').forEach(function (item) {
    if (item.get('value') == valueLoc) {
      label = item.get('label');
    }
  });
  return label;
});

// add space to vertical bar helper
Ember.Handlebars.registerHelper('addSpace', function (property) {
  return Ember.get(this, property).replace(/\|/g, ' | ');
});

// date format helper
Ember.Handlebars.registerHelper("date", function (property) {
  var d = new Date(parseInt(Ember.get(this, property), 10));
  var m_names = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

  var curr_date = d.getDate();
  var curr_month = d.getMonth();
  var curr_year = d.getFullYear();
  return curr_date + " " + m_names[curr_month] + " " + curr_year;
});

// format used in devices table
Ember.Handlebars.registerHelper("date1", function (property) {
  var d, curr_date, curr_month, curr_year, curr_hour, curr_min, monthString, dateString, hourString, minString;
  if (Ember.get(this, property) !== null) {
    d = new Date(parseInt(Ember.get(this, property), 10));
    curr_date = d.getDate();
    curr_month = d.getMonth() + 1;
    curr_year = d.getFullYear();
    curr_hour = d.getHours();
    curr_min = d.getMinutes();

    if (curr_month < 10) {
      monthString = "0" + curr_month.toString();
    } else {
      monthString = curr_month.toString();
    }

    if (curr_date < 10) {
      dateString = "0" + curr_date.toString();
    } else {
      dateString = curr_date.toString();
    }

    if (curr_hour < 10) {
      hourString = "0" + curr_hour.toString();
    } else {
      hourString = curr_hour.toString();
    }

    if (curr_min < 10) {
      minString = "0" + curr_min.toString();
    } else {
      minString = curr_min.toString();
    }

    return curr_year + "-" + monthString + "-" + dateString + "  " + hourString + ":" + minString;
  } else {
    return "";
  }
});

// format used in devices table
Ember.Handlebars.registerHelper("date3", function (property) {
  var d, curr_date, curr_month, curr_year, monthString, dateString;
  if (Ember.get(this, property) !== null) {
    return renderTimeStamp(Ember.get(this, property));
  }
});

FLOW.parseJSON = function(jsonString, property) {
  try {
    var jsonObject = JSON.parse(jsonString);
    if (jsonObject[property].length > 0) {
        return jsonObject;
    } else {
      return null;
    }
  } catch (e) {
    return null;
  }
};

FLOW.addExtraMapBoxTileLayer = function(baseLayers) {
  if (FLOW.Env.extraMapboxTileLayerMapId
      && FLOW.Env.extraMapboxTileLayerAccessToken
      && FLOW.Env.extraMapboxTileLayerLabel) {
    var templateURL = 'https://{s}.tiles.mapbox.com/v4/' +
      FLOW.Env.extraMapboxTileLayerMapId +
      '/{z}/{x}/{y}.jpg?access_token=' +
      FLOW.Env.extraMapboxTileLayerAccessToken;
    var attribution = '<a href="http://www.mapbox.com/about/maps/" target="_blank">Terms &amp; Feedback</a>'
    baseLayers[FLOW.Env.extraMapboxTileLayerLabel] = L.tileLayer(templateURL, {
      attribution: attribution
    })
  }
}

FLOW.drawGeoShape = function(containerNode, geoShapeObject){
  containerNode.style.height = "150px";

  var geoshapeCoordinatesArray, geoShapeObjectType = geoShapeObject['features'][0]['geometry']['type'];
  if(geoShapeObjectType === "Polygon"){
    geoshapeCoordinatesArray = geoShapeObject['features'][0]['geometry']['coordinates'][0];
  } else {
    geoshapeCoordinatesArray = geoShapeObject['features'][0]['geometry']['coordinates'];
  }
  var points = [];

  for(var j=0; j<geoshapeCoordinatesArray.length; j++){
    points.push([geoshapeCoordinatesArray[j][1], geoshapeCoordinatesArray[j][0]]);
  }

  var center = FLOW.getCentroid(points);

  var geoshapeMap = L.map(containerNode, {scrollWheelZoom: false}).setView(center, 2);

  geoshapeMap.options.maxZoom = 18;
  geoshapeMap.options.minZoom = 2;
  var mbAttr = 'Map &copy; 1987-2014 <a href="http://developer.here.com">HERE</a>';
  var mbUrl = 'https://{s}.{base}.maps.cit.api.here.com/maptile/2.1/maptile/{mapID}/{scheme}/{z}/{x}/{y}/256/{format}?app_id={app_id}&app_code={app_code}';
  var normal = L.tileLayer(mbUrl, {
    scheme: 'normal.day.transit',
    format: 'png8',
    attribution: mbAttr,
    subdomains: '1234',
    mapID: 'newest',
    app_id: FLOW.Env.hereMapsAppId,
    app_code: FLOW.Env.hereMapsAppCode,
    base: 'base'
  }).addTo(geoshapeMap);
  var satellite  = L.tileLayer(mbUrl, {
    scheme: 'hybrid.day',
    format: 'jpg',
    attribution: mbAttr,
    subdomains: '1234',
    mapID: 'newest',
    app_id: FLOW.Env.hereMapsAppId,
    app_code: FLOW.Env.hereMapsAppCode,
    base: 'aerial'
  });
  var baseLayers = {
    "Normal": normal,
    "Satellite": satellite
  };

  FLOW.addExtraMapBoxTileLayer(baseLayers);

  L.control.layers(baseLayers).addTo(geoshapeMap);

  //Draw geoshape based on its type
  if(geoShapeObjectType === "Polygon"){
    var geoShapePolygon = L.polygon(points).addTo(geoshapeMap);
    geoshapeMap.fitBounds(geoShapePolygon.getBounds());
  }else if (geoShapeObjectType === "MultiPoint") {
    var geoShapeMarkersArray = [];
    for (var i = 0; i < points.length; i++) {
      geoShapeMarkersArray.push(L.marker([points[i][0],points[i][1]]));
    }
    var geoShapeMarkers = L.featureGroup(geoShapeMarkersArray).addTo(geoshapeMap);
    geoshapeMap.fitBounds(geoShapeMarkers.getBounds());
  }else if (geoShapeObjectType === "LineString") {
    var geoShapeLine = L.polyline(points).addTo(geoshapeMap);
    geoshapeMap.fitBounds(geoShapeLine.getBounds());
  }
};

FLOW.getCentroid = function (arr) {
  return arr.reduce(function (x,y) {
    return [x[0] + y[0]/arr.length, x[1] + y[1]/arr.length]
  }, [0,0])
}

Ember.Handlebars.registerHelper("getServer", function () {
  var loc = window.location.href,
    pos = loc.indexOf("/admin");
  return loc.substring(0, pos);
});

Ember.Handlebars.registerHelper('sgName', function (property) {
  var sgId = Ember.get(this, property),
      sg = FLOW.surveyGroupControl.find(function (item) {
        return item.get && item.get('keyId') === sgId;
      });
  return sg && sg.get('name') || sgId;
});

// Register a Handlebars helper that instantiates `view`.
// The view will have its `content` property bound to the
// helper argument.
FLOW.registerViewHelper = function (name, view) {
  Ember.Handlebars.registerHelper(name, function (property, options) {
    options.hash.contentBinding = property;
    return Ember.Handlebars.helpers.view.call(this, view, options);
  });
};


FLOW.registerViewHelper('date2', Ember.View.extend({
  tagName: 'span',

  template: Ember.Handlebars.compile('{{view.formattedContent}}'),

  formattedContent: (function () {
    var content, d, curr_date, curr_month, curr_year, curr_hour, curr_min, monthString, dateString, hourString, minString;
    content = this.get('content');

    if (content === null) {
      return "";
    }

    d = new Date(parseInt(content, 10));
    curr_date = d.getDate();
    curr_month = d.getMonth() + 1;
    curr_year = d.getFullYear();
    curr_hour = d.getHours();
    curr_min = d.getMinutes();

    if (curr_month < 10) {
      monthString = "0" + curr_month.toString();
    } else {
      monthString = curr_month.toString();
    }

    if (curr_date < 10) {
      dateString = "0" + curr_date.toString();
    } else {
      dateString = curr_date.toString();
    }

    if (curr_hour < 10) {
      hourString = "0" + curr_hour.toString();
    } else {
      hourString = curr_hour.toString();
    }

    if (curr_min < 10) {
      minString = "0" + curr_min.toString();
    } else {
      minString = curr_min.toString();
    }

    return curr_year + "-" + monthString + "-" + dateString + "  " + hourString + ":" + minString;
  }).property('content')
}));





// ********************************************************//
//                      main navigation
// ********************************************************//
FLOW.NavigationView = Em.View.extend({
  templateName: 'application/navigation',
  selectedBinding: 'controller.selected',

  showMapsButton: function () {
      return FLOW.Env.showMapsTab;
  }.property('FLOW.Env.showMapsTab'),

  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:current navItem'.w(),

    navItem: function () {
      return this.get('item');
    }.property('item').cacheable(),

    isActive: function () {
      return this.get('item') === this.get('parentView.selected');
    }.property('item', 'parentView.selected').cacheable(),

    showDevicesButton: function () {
      return FLOW.permControl.get('canManageDevices');
    }.property(),

    eventManager: Ember.Object.create({
      click: function(event, clickedView) {

        // Add the active tab as a CSS class to html
        var html = document.querySelector('html');
        html.className = '';
        html.classList.add(FLOW.router.navigationController.selected);
      }
    }),
  }),

});

// ********************************************************//
//                      standard views
// ********************************************************//
// TODO check if doing this in View is not impacting performance,
// as some pages have a lot of views (all navigation elements, for example)
// one way could be use an extended copy of view, with the didInsertElement,
// for some of the elements, and not for others.
Ember.View.reopen({
  didInsertElement: function () {
    this._super();
    tooltip();
  }
});

Ember.Select.reopen({
  attributeBindings: ['size']
});


FLOW.DateField = Ember.TextField.extend({
  minDate: true,

  didInsertElement: function () {
    this._super();

    if (this.get('minDate')) {
      // datepickers with only future dates
      $("#from_date, #from_date02").datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        minDate: new Date(),
        onSelect: function (selectedDate) {
          $("#to_date, #to_date02").datepicker("option", "minDate", selectedDate);
          FLOW.dateControl.set('fromDate', selectedDate);
        }
      });

      $("#to_date, #to_date02").datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        minDate: new Date(),
        onSelect: function (selectedDate) {
          $("#from_date, #from_date02").datepicker("option", "maxDate", selectedDate);
          FLOW.dateControl.set('toDate', selectedDate);
        }
      });
    } else {
      // datepickers with all dates
      $("#from_date, #from_date02").datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        onSelect: function (selectedDate) {
          $("#to_date, #to_date02").datepicker("option", "minDate", selectedDate);
          FLOW.dateControl.set('fromDate', selectedDate);
        }
      });

      $("#to_date, #to_date02").datepicker({
        dateFormat: 'yy-mm-dd',
        defaultDate: new Date(),
        numberOfMonths: 1,
        onSelect: function (selectedDate) {
          $("#from_date, #from_date02").datepicker("option", "maxDate", selectedDate);
          FLOW.dateControl.set('toDate', selectedDate);
        }
      });
    }
  }
});

FLOW.DateField2 = Ember.TextField.extend({
  didInsertElement: function () {
    this._super();

    this.$().datepicker({
      dateFormat: 'yy-mm-dd',
      defaultDate: new Date(),
      numberOfMonths: 1
    });
  }
});

// home screen view
FLOW.NavHomeView = Ember.View.extend({
  templateName: 'navHome/nav-home'
});

// project views
FLOW.NavProjectsView = Ember.View.extend({
  templateName: 'navProjects/nav-projects-main'
});

// surveys views
FLOW.NavSurveysView = Ember.View.extend({
  templateName: 'navSurveys/nav-surveys'
});
FLOW.NavSurveysMainView = Ember.View.extend({
  templateName: 'navSurveys/nav-surveys-main'
});

FLOW.NavSurveysEditView = Ember.View.extend({
  templateName: 'navSurveys/nav-surveys-edit'
});

FLOW.ManageNotificationsView = Ember.View.extend({
  templateName: 'navSurveys/manage-notifications'
});

FLOW.ManageTranslationsView = Ember.View.extend({
  templateName: 'navSurveys/manage-translations'
});

FLOW.EditQuestionsView = Ember.View.extend({
  templateName: 'navSurveys/edit-questions'
});

// devices views
FLOW.NavDevicesView = Ember.View.extend({
  templateName: 'navDevices/nav-devices'
});

FLOW.CurrentDevicesView = FLOW.View.extend({
  templateName: 'navDevices/devices-list-tab/devices-list'
});

FLOW.AssignSurveysOverviewView = FLOW.View.extend({
  templateName: 'navDevices/assignment-list-tab/assignment-list'
});

FLOW.EditSurveyAssignmentView = Ember.View.extend({
  templateName: 'navDevices/assignment-edit-tab/assignment-edit'
});

FLOW.SurveyBootstrapView = FLOW.View.extend({
  templateName: 'navDevices/bootstrap-tab/survey-bootstrap'
});

// data views
FLOW.NavDataView = Ember.View.extend({
  templateName: 'navData/nav-data'
});

FLOW.InspectDataView = Ember.View.extend({
  templateName: 'navData/inspect-data'
});

FLOW.BulkUploadView = Ember.View.extend({
  templateName: 'navData/bulk-upload'
});
FLOW.DataCleaningView = Ember.View.extend({
  templateName: 'navData/data-cleaning'
});

FLOW.CascadeResourcesView = Ember.View.extend({
	  templateName: 'navData/cascade-resources'
});

FLOW.MonitoringDataView = Ember.View.extend({
  templateName: 'navData/monitoring-data'
});

// reports views
FLOW.NavReportsView = Ember.View.extend({
  templateName: 'navReports/nav-reports'
});

FLOW.ExportReportsView = Ember.View.extend({
  templateName: 'navReports/export-reports'
});

FLOW.ChartReportsView = Ember.View.extend({
  templateName: 'navReports/chart-reports'
});

// applets
FLOW.BootstrapApplet = Ember.View.extend({
  templateName: 'navDevices/bootstrap-tab/applets/bootstrap-applet'
});

FLOW.rawDataReportApplet = Ember.View.extend({
  templateName: 'navReports/applets/raw-data-report-applet'
});

FLOW.comprehensiveReportApplet = Ember.View.extend({
  templateName: 'navReports/applets/comprehensive-report-applet'
});

FLOW.googleEarthFileApplet = Ember.View.extend({
  templateName: 'navReports/applets/google-earth-file-applet'
});

FLOW.surveyFormApplet = Ember.View.extend({
  templateName: 'navReports/applets/survey-form-applet'
});

FLOW.bulkImportApplet = Ember.View.extend({
  templateName: 'navData/applets/bulk-import-applet'
});

FLOW.rawDataImportApplet = Ember.View.extend({
  templateName: 'navData/applets/raw-data-import-applet'
});

// users views
FLOW.NavUsersView = Ember.View.extend({
  templateName: 'navUsers/nav-users'
});

// Messages views
FLOW.NavMessagesView = Ember.View.extend({
  templateName: 'navMessages/nav-messages'
});

// admin views
FLOW.NavAdminView = FLOW.View.extend({
  templateName: 'navAdmin/nav-admin'
});

FLOW.HeaderView = FLOW.View.extend({
  templateName: 'application/header-common'
});

FLOW.FooterView = FLOW.View.extend({
  templateName: 'application/footer'
});

// ********************************************************//
//             Subnavigation for the Data tabs
// ********************************************************//
FLOW.DatasubnavView = FLOW.View.extend({
  templateName: 'navData/data-subnav',
  selectedBinding: 'controller.selected',
  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:active'.w(),

    isActive: function () {
      return this.get('item') === this.get('parentView.selected');
    }.property('item', 'parentView.selected').cacheable(),

    showDataCleaningButton: function () {
        return FLOW.permControl.get('canCleanData');
    }.property(),

    showCascadeResourcesButton: function () {
      return FLOW.permControl.get('canManageCascadeResources');
    }.property(),

    showDataApprovalButton: function () {
        return FLOW.Env.enableDataApproval && FLOW.permControl.get('canManageDataAppoval');
    }.property(),
  })
});

// ********************************************************//
//             Subnavigation for the Device tabs
// ********************************************************//
FLOW.DevicesSubnavView = FLOW.View.extend({
  templateName: 'navDevices/devices-subnav',
  selectedBinding: 'controller.selected',
  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:active'.w(),

    isActive: function () {
      return this.get('item') === this.get('parentView.selected');
    }.property('item', 'parentView.selected').cacheable()
  })
});

// ********************************************************//
//             Subnavigation for the Reports tabs
// ********************************************************//
FLOW.ReportsSubnavView = Em.View.extend({
  templateName: 'navReports/reports-subnav',
  selectedBinding: 'controller.selected',
  NavItemView: Ember.View.extend({
    tagName: 'li',
    classNameBindings: 'isActive:active'.w(),

    isActive: function () {
      return this.get('item') === this.get('parentView.selected');
    }.property('item', 'parentView.selected').cacheable()
  })
});


// *************************************************************//
//             Generic table column view which supports sorting
// *************************************************************//
FLOW.ColumnView = Ember.View.extend({
  tagName: 'th',
  item: null,
  type: null,

  classNameBindings: ['isActiveAsc:sorting_asc', 'isActiveDesc:sorting_desc'],

  isActiveAsc: function () {
    return this.get('item') === FLOW.tableColumnControl.get('selected') && FLOW.tableColumnControl.get('sortAscending') === true;
  }.property('item', 'FLOW.tableColumnControl.selected', 'FLOW.tableColumnControl.sortAscending').cacheable(),

  isActiveDesc: function () {
    return this.get('item') === FLOW.tableColumnControl.get('selected') && FLOW.tableColumnControl.get('sortAscending') === false;
  }.property('item', 'FLOW.tableColumnControl.selected', 'FLOW.tableColumnControl.sortAscending').cacheable(),

  sort: function () {
    if ((this.get('isActiveAsc')) || (this.get('isActiveDesc'))) {
      FLOW.tableColumnControl.toggleProperty('sortAscending');
    } else {
      FLOW.tableColumnControl.set('sortProperties', [this.get('item')]);
      FLOW.tableColumnControl.set('selected', this.get('item'));
    }

    if (this.get('type') === 'device') {
      FLOW.deviceControl.getSortInfo();
    } else if (this.get('type') === 'assignment') {
      FLOW.surveyAssignmentControl.getSortInfo();
    } else if (this.get('type') === 'message') {
      FLOW.messageControl.getSortInfo();
    }
  }
});

var set = Ember.set,
  get = Ember.get;
Ember.RadioButton = Ember.View.extend({
    tagName : 'input',
    type : 'radio',
    attributeBindings : ['name', 'type', 'value', 'checked:checked:'],
    click : function() {
        this.set('selection', this.$().val());
    },
    checked : function() {
        return this.get('value') === this.get('selection');
    }.property()
});

FLOW.SelectFolder = Ember.Select.extend({
  controller: null,

  init: function() {
    this._super();
    this.set('prompt', Ember.String.loc('_choose_folder_or_survey'));
    this.set('optionLabelPath', 'content.code');
    this.set('optionValuePath', 'content.keyId');
    this.set('controller', FLOW.SurveySelection.create({ selectionFilter: this.get('selectionFilter')}));
    this.set('content', this.get('controller').getByParentId(this.get('parentId'), this.get('showMonitoringSurveysOnly')));
  },

  onChange: function() {
    var childViews = this.get('parentView').get('childViews');
    var keyId = this.get('value');
    var survey = this.get('controller').getSurvey(keyId);
    var nextIdx = this.get('idx') + 1;
    var monitoringOnly = this.get('showMonitoringSurveysOnly');
    var filter = this.get('selectionFilter');

    if (nextIdx !== childViews.length) {
      childViews.removeAt(nextIdx, childViews.length - nextIdx);
    }

    if (this.get('controller').isSurvey(keyId)) {
      FLOW.selectedControl.set('selectedSurveyGroup', survey);
      if (FLOW.Env.enableDataApproval && survey.get('dataApprovalGroupId')) {
          FLOW.router.approvalGroupController.load(survey.get('dataApprovalGroupId'));
          FLOW.router.approvalStepsController.loadByGroupId(survey.get('dataApprovalGroupId'));
      }
    } else {
      FLOW.selectedControl.set('selectedSurveyGroup', null);
      childViews.pushObject(FLOW.SelectFolder.create({
        parentId: keyId,
        idx: nextIdx,
        showMonitoringSurveysOnly: monitoringOnly,
        selectionFilter : filter
      }));
    }
  }.observes('value'),
});


FLOW.SurveySelectionView = Ember.ContainerView.extend({
  tagName: 'div',
  classNames: 'modularSelection',
  childViews: [],

  init: function() {
    this._super();
    this.get('childViews').pushObject(FLOW.SelectFolder.create({
      parentId: 0, // start with the root folder
      idx: 0,
      showMonitoringSurveysOnly: this.get('showMonitoringSurveysOnly') || false
    }));
  },
})


FLOW.DataCleaningSurveySelectionView = Ember.ContainerView.extend({
  tagName: 'div',
  classNames: 'modularSelection',
  childViews: [],

  init: function() {
    this._super();
    this.get('childViews').pushObject(FLOW.SelectFolder.create({
      parentId: 0, // start with the root folder
      idx: 0,
      showMonitoringSurveysOnly: this.get('showMonitoringSurveysOnly') || false,
      selectionFilter : FLOW.projectControl.dataCleaningEnabled
    }));
  },
})

});
