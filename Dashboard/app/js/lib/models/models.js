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
	//adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"REST"})
	adapter: DS.fixtureAdapter
});

FLOW.SurveyGroup = DS.Model.extend({
	surveyList: DS.attr('string',{defaulValue: ""}),
	description: DS.attr('string',{defaulValue: ""}),
	name: DS.attr('string',{defaulValue: ""}),
	createdDateTime: DS.attr('string',{defaulValue: ""}),
	lastUpdateDateTime: DS.attr('string',{defaulValue: ""}),
	code: DS.attr('string',{defaulValue: ""}),
	displayName: DS.attr('string',{defaulValue: ""}),
	keyId: DS.attr('number'),
	primaryKey: 'keyId',

});


FLOW.SurveyGroup.FIXTURES = [{
	keyId: 1,
	displayName: 'Urban sanitation surveys',
}, {
	keyId: 2,
	displayName: 'Elise Surveys',
}, {
	keyId: 3,
	displayName: 'Test Survey group',
},{
	keyId: 4,
	displayName: 'Upande - SNVVERMIS',
},{
	keyId: 5,
	displayName: 'Akvo test surveys',
},

];


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
	displayName: 'Water point survey',
	name:'Water point survey',
	surveyGroupId: 1
}, {
	keyId: 2,
	displayName: 'Sanitation survey',
	name:'Sanitation survey',
	surveyGroupId: 1
}, {
	keyId: 3,
	displayName: 'Baseline WASH',
	name:'Baseline WASH',
	surveyGroupId: 1
}, {
	keyId: 4,
	displayName: 'Akvo RSR update',
	name:'Akvo RSR update',
	surveyGroupId: 1
}, 


];

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
	order:1,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu. ",
	name: 'Location',
	displayName: 'Location',

}, {
	keyId: 2,
	order:3,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
	name: 'Occupation',
	displayName: 'Occupation',

}, {
	keyId: 3,
	order:2,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
	name: 'Water system',
	displayName: 'Water system',

}, {
	keyId: 4,
	order:4,
	description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin in ligula et ipsum feugiat egestas ac vel arcu.",
	name: 'Sanitation system',
	displayName: 'Sanitation system',

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
	questionTypeString: DS.attr('string',{defaultValue:"freeText"}),
	surveyId: DS.attr('number'),
	displayName: DS.attr('string'),
	keyId: DS.attr('number'),
});


FLOW.Question.FIXTURES = [{
	keyId: 1,
	text: 'What is the name of the community?',
	displayName: 'What is the name of the community?',
	mandatory: false,
	questionTypeString:'freeText',
	questionSetId: 1
}, {
	keyId: 2,
	text: 'What is your occupation?',
	displayName: 'What is your occupation?',
	mandatory: false,
	questionTypeString:'option',
	questionSetId: 1
}, {
	keyId: 3,
	text: 'How much children do you have?',
	displayName: 'How much children do you have?',
	mandatory: false,
	questionTypeString:'number',
	questionSetId: 1
}, {
	keyId: 4,
	text: 'Please take a geolocation',
	displayName: 'Please take a geolocation',
	mandatory: false,
	questionTypeString:'geoLoc',
	questionSetId: 1
}, 	{
	keyId: 4,
	text: 'Please take a picture',
	displayName: 'Please take a picture',
	mandatory: false,
	questionTypeString:'photo',
	questionSetId: 1
},	{
	keyId: 4,
	text: 'Please make a video',
	displayName: 'Please make a video',
	questionTypeString:'video',
	mandatory: false,
	questionSetId: 1
},	{
	keyId: 4,
	text: 'What is the date today?',
	displayName: 'What is the date today?',
	questionTypeString:'date',
	mandatory: false,
	questionSetId: 1
}];

FLOW.QuestionOption = DS.Model.extend({
	questionId: DS.attr('number'),
	text: DS.attr('string'),
	keyId: DS.attr('number'),
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