import ReactDOM from 'react-dom';

FLOW.ReactComponentView = Ember.View.extend({
  template: Ember.Handlebars.compile(''),

  reactRender(reactComponent) {
    ReactDOM.render(reactComponent, this.get('element'));
  },

  unmountReactElement() {
    ReactDOM.unmountComponentAtNode(this.get('element'));
  },

  willDestroyElement() {
    this._super();
    this.unmountReactElement();
  },
});
