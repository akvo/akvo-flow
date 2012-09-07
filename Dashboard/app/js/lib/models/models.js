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
	revision: 4,
	adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"REST"})
	//adapter: DS.fixtureAdapter
});

FLOW.SurveyGroup = DS.Model.extend({
	surveyList: DS.attr('string'),
	description: DS.attr('string'),
	name: DS.attr('string'),
	createdDateTime: DS.attr('string'),
	lastUpdateDateTime: DS.attr('string'),
	code: DS.attr('string'),
	displayName: DS.attr('string'),
	keyId: DS.attr('number'),
	primaryKey: 'keyId',

});


FLOW.SurveyGroup.FIXTURES = [{
	keyId: 1,
	displayName: 'mark westra',
}, {
	keyId: 2,
	displayName: 'Linda porsius',
}, ];


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
	primaryKey: 'keyId',

});

FLOW.Survey.FIXTURES = [{
	keyId: 1,
	displayName: 'Carel van Hassel',
	surveyGroupId: 1
}, {
	keyId: 2,
	displayName: 'Thomas van Hassel',
	surveyGroupId: 2
}, ];

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
		defaulValue: 0
	}),
	primaryKey: 'keyId',
});


FLOW.QuestionGroup.FIXTURES = [{
	keyId: 1,
	isOpen: 0,
	description: "lorem",
	name: 'question set 1',
	displayName: 'question set 1',

}, {
	keyId: 2,
	isOpen: 0,
	description: "ipsum",
	name: 'question set 2',
	displayName: 'question set 2',

}, {
	keyId: 3,
	description: "dolor",
	name: 'question set 3',
	displayName: 'question set 3',

}, {
	keyId: 4,
	description: "dolor",
	name: 'question set 4',
	displayName: 'question set 4',

}, ];



FLOW.Question = DS.Model.extend({
	translationMap: DS.attr('string'),
	// not sure 
	allowDecimal: DS.attr('boolean', {defaulValue: 0}),
	optionContainerDto: DS.attr('string'),
	//not sure
	// type:     --- not now
	tip: DS.attr('string'),
	maxVal: DS.attr('number'),
	minVal: DS.attr('number'),
	order: DS.attr('number'),
	isName: DS.attr('boolean', {defaulValue: 0}),
	// questionHelpList:     --- now now
	collabseable: DS.attr('boolean', {defaulValue: 0}),
	path: DS.attr('string'),
	allowMultipleFlag: DS.attr('boolean', {defaulValue: 0}),
	immutable: DS.attr('boolean', {defaulValue: 0}),
	allowOtherFlag: DS.attr('boolean', {defaulValue: 0}),
	allowSign: DS.attr('boolean', {defaulValue: 0}),
	text: DS.attr('string'),
	class: DS.attr('string'),
	questionDependency: DS.attr('string'),
	mandatoryFlag: DS.attr('boolean', {defaulValue: 0}),
	questionGroupId: DS.attr('number'),
	questionTypeString: DS.attr('string'),
	surveyId: DS.attr('number'),
	displayName: DS.attr('string'),
	keyId: DS.attr('number'),
});


FLOW.Question.FIXTURES = [{
	keyId: 1,
	text: 'question 1 of set 1',
	displayName: 'name1',
	mandatory: false,
	questionSetId: 1
}, {
	keyId: 2,
	text: 'question 2 of set 1',
	displayName: 'name2',
	mandatory: false,
	questionSetId: 1
}, {
	keyId: 3,
	text: 'question 1 of set 2',
	displayName: 'name3',
	mandatory: false,
	questionSetId: 2
}, {
	keyId: 4,
	text: 'question 2 of set 2',
	displayName: 'name4',
	mandatory: false,
	questionSetId: 2
}, ];
