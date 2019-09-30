// mock globals variables
const FLOW = {
  deviceGroupControl: {
    content: {
      isLoaded: true,
    },
    get: jest.fn().mockReturnValue({
      forEach: jest.fn(cb =>
        cb({
          get: jest.fn((key) => {
            switch (key) {
            case 'keyId':
              return 1;
            case 'code':
              return 'Devices not in a group';
            default:
              return null;
            }
          }),
        })),
    }),
  },

  deviceControl: {
    content: {
      isLoaded: true,
    },
    get: jest.fn().mockReturnValue({
      forEach: jest.fn(cb =>
        cb({
          get: jest.fn((key) => {
            switch (key) {
            case 'keyId':
              return 150452032;
            case 'deviceGroup':
              return 1;
            case 'deviceIdentifier':
              return 'jana';
            default:
              return null;
            }
          }),
        })),
    }),
  },

  selectedControl: {
    selectedSurveyAssignment: {
      get: jest.fn((key) => {
        switch (key) {
        case 'deviceIds':
          return [150452032];
        default:
          return null;
        }
      }),
    },

    selectedDevices: {
      pushObject: jest.fn(),
      removeObject: jest.fn(),
    },
  },

  Device: {
    find: jest.fn().mockReturnValue({
      get: jest.fn((key) => {
        switch (key) {
        case 'keyId':
          return 150452032;
        default:
          return null;
        }
      }),
    }),
  },
};

global.FLOW = FLOW;

export default { FLOW };
