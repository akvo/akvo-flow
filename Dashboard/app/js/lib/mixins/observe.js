import { isArray } from 'lodash';

export default mappings => Ember.Mixin.create({
  init() {
    this._super();
    Object.keys(mappings).forEach((key) => {
      const handler = mappings[key];
      (isArray(handler) ? handler : [handler]).forEach((h) => {
        this.addObserver(key, this, h);
      });
    });
  },
});
