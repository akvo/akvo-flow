// ***********************************************//
//                 models and stores
// ***********************************************//
require('akvo-flow/core');
DS.Model.reopen({
	namingConvention: {
		keyToJSONKey: function(key) {
			return key;
		},

		foreignKey: function(key) {
			return key;
		}
	}
});

FLOW.store = DS.Store.create({
	revision: 7,
	//adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"rest", url:"http://localhost"})
	adapter: DS.fixtureAdapter
});

//DS.fixtureAdapter.map('FLOW.SurveyGroup',{
//
//primaryKey: 'keyId'
//	});

FLOW.SurveyGroup = DS.Model.extend({
	surveyList: DS.attr('string',{defaultValue: ""}),
	description: DS.attr('string',{defaultValue: ""}),
	name: DS.attr('string',{defaultValue: ""}),
	createdDateTime: DS.attr('string',{defaultValue: ""}),
	lastUpdateDateTime: DS.attr('string',{defaultValue: ""}),
	code: DS.attr('string',{defaultValue: ""}),
	displayName: DS.attr('string',{defaultValue: ""}),
	keyId: DS.attr('number'),
	primaryKey: 'keyId'

});


FLOW.Survey = DS.Model.extend({
	defaultLanguageCode: DS.attr('string'),
	status: DS.attr('string'),
	code: DS.attr('string'),
	requireApproval: DS.attr('string'),
	version: DS.attr('string'),
	description: DS.attr('string'),
	name: DS.attr('string'),
	path: DS.attr('string'),
	displayName: DS.attr('string'),
	questionGroupList: DS.attr('string'),
	keyId: DS.attr('number'),
	pointType: DS.attr('string'),
	surveyGroupId: DS.attr('number'),
	primaryKey: 'keyId'

});


FLOW.QuestionGroup = DS.Model.extend({
	order: DS.attr('number'),
	questionMap: DS.attr('string'),
	description: DS.attr('string'),
	name: DS.attr('string'),
	path: DS.attr('string'),
	class: DS.attr('string'),
	code: DS.attr('string'),
	surveyId: DS.attr('number'),
	displayName: DS.attr('string'),
	keyId: DS.attr('number'),
	isOpen: DS.attr('boolean', {
		defaultValue: 0
	}),
	primaryKey: 'keyId'
});


FLOW.Question = DS.Model.extend({
	translationMap: DS.attr('string'),
	// not sure
	allowDecimal: DS.attr('boolean', {defaultValue: 0}),
	optionContainerDto: DS.attr('string'),
	//not sure
	// type:     --- not now
	tip: DS.attr('string'),
	maxVal: DS.attr('number'),
	minVal: DS.attr('number'),
	order: DS.attr('number'),
	isName: DS.attr('boolean', {defaultValue: 0}),
	// questionHelpList:     --- now now
	collabseable: DS.attr('boolean', {defaultValue: 0}),
	path: DS.attr('string'),
	allowMultipleFlag: DS.attr('boolean', {defaultValue: 0}),
	immutable: DS.attr('boolean', {defaultValue: 0}),
	allowOtherFlag: DS.attr('boolean', {defaultValue: 0}),
	allowSign: DS.attr('boolean', {defaultValue: 0}),
	text: DS.attr('string'),
	class: DS.attr('string'),
	questionDependency: DS.attr('string'),
	mandatoryFlag: DS.attr('boolean', {defaultValue: 0}),
	questionGroupId: DS.attr('number'),
	questionTypeString: DS.attr('string',{defaultValue:"freeText"}),
	surveyId: DS.attr('number'),
	displayName: DS.attr('string'),
	keyId: DS.attr('number')
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
	lastUpdate: DS.attr('string',{defaultValue: ""}),	//should be DS.attr('date'),
	osVersion: DS.attr('string',{defaultValue: ""}),
	lastKnownLat: DS.attr('number',{defaultValue: 0}),
	lastKnownLong:DS.attr('number',{defaultValue: 0}),
	lastKnownAccuracy: DS.attr('number',{defaultValue:0}),
	lastLocationBeaconTime: DS.attr('string',{defaultValue: ""}), //should be DS.attr('date'),
	deviceGroup: DS.attr('string',{defaultValue: ""}),
	keyId: DS.attr('number'),
	primaryKey:'keyId',
	isSelected: DS.attr('boolean', {defaultValue: false})
});


