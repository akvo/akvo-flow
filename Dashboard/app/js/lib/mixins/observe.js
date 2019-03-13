import { isArray, isString } from 'lodash';

export default mappings => Ember.Mixin.create({
  init() {
    this._super();
    Object.keys(mappings).forEach((key) => {
      if (!key || (isString(key) && !key.length)) {
        throw new Error('observer key not set');
      }
      const handler = mappings[key];
      if (!handler || (isString(handler) && !handler.length)) {
        throw new Error('observer method not set');
      }
      (isArray(handler) ? handler : [handler]).forEach((h) => {
        this.addObserver(key, this, h);
      });
    });
  },
});
