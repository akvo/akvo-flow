/* eslint-disable global-require, import/no-dynamic-require */

export default templateName => Ember.Mixin.create({
  template: Ember.Handlebars.compile(require(`templates/${templateName}`)),
});
