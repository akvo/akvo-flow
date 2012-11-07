// ***********************************************//
//                 models and stores
// ***********************************************//
require('akvo-flow/core');
// DS.Model.reopen({
//	namingConvention: {
//		keyToJSONKey: function(key) {
//			return key;
//		},

//		foreignKey: function(key) {
//			return key;
//		}
//	}
// });

FLOW.store = DS.Store.create({
	revision: 7,
	//adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"rest", url:"http://localhost"}),
	adapter: DS.fixtureAdapter

});

FLOW.SurveyGroup = DS.Model.extend({
	description: DS.attr('string',{defaultValue: ""}),
	name: DS.attr('string',{defaultValue: ""}),
	createdDateTime: DS.attr('string',{defaultValue: ""}),
	lastUpdateDateTime: DS.attr('string',{defaultValue: ""}),
	// the code field is used as name
	code: DS.attr('string',{defaultValue: ""}),
	keyId: DS.attr('number')
});


FLOW.Survey = DS.Model.extend({
	defaultLanguageCode: DS.attr('string'),
	status: DS.attr('string'),
	code: DS.attr('string'),
	requireApproval: DS.attr('string'),
	version: DS.attr('string'),
	description: DS.attr('string'),
	name: DS.attr('string'),
	displayName: DS.attr('string'),
	keyId: DS.attr('number'),
	pointType: DS.attr('string'),
	surveyGroupId: DS.attr('number'),
	createdDateTime:DS.attr('number'),
	lastUpdateDateTime:DS.attr('number'),
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

FLOW.QuestionOption.FIXTURES = [{
	keyId: 1,
	text: 'teacher',
	questionId: 1
}, {
	keyId: 2,
	text: 'cook',
	questionId: 1
},{
	keyId: 3,
	text: 'minister',
	questionId: 1
},{
	keyId: 4,
	text: 'programmer',
	questionId: 1
}];

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

FLOW.Location = DS.Model.extend({
  keyId:      DS.attr("number"),
  label:      DS.attr("string"),
  latitude:   DS.attr("number"),
  longitude:  DS.attr("number")
});

FLOW.Location.FIXTURES = [{
  keyId:      1,
  label:      "Edinburgh",
  latitude:   55.953252,
  longitude:  -3.188267
},{
  keyId:      2,
  label:      "Reykjavík",
  latitude:   64.135338,
  longitude:  -21.89521
}];
