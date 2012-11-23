// ***********************************************//
//                 models and stores
// ***********************************************//
require('akvo-flow/core');
require('akvo-flow/models/store_def');

FLOW.SurveyGroup = DS.Model.extend({
    didDelete: function() {FLOW.surveyGroupControl.populate();},
    didUpdate: function() {FLOW.surveyGroupControl.populate();},
    didCreate: function() {FLOW.surveyGroupControl.populate();},

	description: DS.attr('string',{defaultValue: ""}),
	name: DS.attr('string',{defaultValue: ""}),
	createdDateTime: DS.attr('string',{defaultValue: ""}),
	lastUpdateDateTime: DS.attr('string',{defaultValue: ""}),
	// the code field is used as name
	code: DS.attr('string',{defaultValue: ""}),
	keyId: DS.attr('number'),
	primaryKey: 'keyId'
});


FLOW.Survey = DS.Model.extend({
	didDelete: function() {FLOW.surveyControl.populate();},
    didUpdate: function() {FLOW.surveyControl.populate();},
    didCreate: function() {FLOW.surveyControl.populate();},

	defaultLanguageCode: DS.attr('string'),
	status: DS.attr('string'),
	code: DS.attr('string'),
	requireApproval: DS.attr('string'),
	version: DS.attr('string'),
	description: DS.attr('string'),
	name: DS.attr('string'),
	keyId: DS.attr('number'),
	pointType: DS.attr('string'),
	surveyGroupId: DS.attr('number'),
	createdDateTime:DS.attr('number'),
	lastUpdateDateTime:DS.attr('number'),
	primaryKey: 'keyId'

});


FLOW.QuestionGroup = DS.Model.extend({
	didDelete: function() {if (FLOW.questionGroupControl.get('allRecordsSaved')) FLOW.questionGroupControl.populate();},
    didUpdate: function() {if (FLOW.questionGroupControl.get('allRecordsSaved')) FLOW.questionGroupControl.populate();},
    didCreate: function() {if (FLOW.questionGroupControl.get('allRecordsSaved')) FLOW.questionGroupControl.populate();},

	order: DS.attr('number'),
	description: DS.attr('string'),
	name: DS.attr('string'),
	path: DS.attr('string'),
	code: DS.attr('string'),
	surveyId: DS.attr('number'),
	keyId: DS.attr('number'),
	//isOpen: DS.attr('boolean', {
	//	defaultValue: 0
	//}),
	primaryKey: 'keyId'
});


FLOW.Question = DS.Model.extend({
	allowDecimal: DS.attr('boolean', {defaultValue: 0}),
	allowMultipleFlag: DS.attr('boolean', {defaultValue: 0}),
	allowOtherFlag: DS.attr('boolean', {defaultValue: 0}),
	allowSign: DS.attr('boolean', {defaultValue: 0}),
	collabseable: DS.attr('boolean', {defaultValue: 0}),
	displayName: DS.attr('string'),
	immutable: DS.attr('boolean', {defaultValue: 0}),
	isName: DS.attr('boolean', {defaultValue: 0}),
	keyId: DS.attr('number'),
	mandatoryFlag: DS.attr('boolean', {defaultValue: 0}),
	maxVal: DS.attr('number'),
	minVal: DS.attr('number'),
	optionContainerDto: DS.attr('string'),
	order: DS.attr('number'),
	path: DS.attr('string'),
	questionDependency: DS.attr('string'),
	questionGroupId: DS.attr('number'),
	questionTypeString: DS.attr('string',{defaultValue:"freeText"}),
	surveyId: DS.attr('number'),
	text: DS.attr('string'),
	tip: DS.attr('string'),
	type: DS.attr('string'),
});

FLOW.QuestionOption = DS.Model.extend({
	questionId: DS.attr('number'),
	text: DS.attr('string'),
	keyId: DS.attr('number')
});


FLOW.SurveyAssignment = DS.Model.extend({

});

FLOW.DeviceGroup = DS.Model.extend({
	displayName: DS.attr('string',{defaultValue: ""}),
	code: DS.attr('string',{defaultValue: ""})
});

FLOW.Device = DS.Model.extend({
	deviceType: DS.attr('string',{defaultValue: ""}),
	phoneNumber: DS.attr('string',{defaultValue: ""}),
	esn: DS.attr('string',{defaultValue: ""}),
	deviceIdentifier: DS.attr('string',{defaultValue: ""}),
	inServiceDate: DS.attr('date'),
	outServiceDate: DS.attr('date'),
	lastUpdate: DS.attr('date', {defaultValue: ""}),
	osVersion: DS.attr('string', {defaultValue: ""}),
	lastKnownLat: DS.attr('number', {defaultValue: 0}),
	lastKnownLong:DS.attr('number', {defaultValue: 0}),
	lastKnownAccuracy: DS.attr('number',{defaultValue:0}),
	lastLocationBeaconTime: DS.attr('date', {defaultValue: ""}),
	deviceGroup: DS.attr('string',{defaultValue: ""}),
	keyId: DS.attr('number'),
	primaryKey:'keyId',
	isSelected: DS.attr('boolean', {defaultValue: false})
});

FLOW.SurveyedLocale = DS.Model.extend({
  description:  DS.attr("string", {defaultValue: ""}),
  keyId:        DS.attr("number"),
  latitude:     DS.attr("number"),
  longitude:    DS.attr("number"),
  primaryKey:   "keyId",
  typeMark:     DS.attr("string", {defaultValue: "WATER_POINT"})
});

FLOW.Placemark = DS.Model.extend({
  keyId: DS.attr("number"),
  latitude: DS.attr("number"),
  longitude: DS.attr("number"),
  collectionDate: DS.attr("date"),
  markType: DS.attr("string", {defaultValue: "WATER_POINT"}),
  primaryKey: "keyId"
});