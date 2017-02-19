import { LOAD_DATA, LOAD_DATA_FAILED, REQUESTING_DATA } from '../constants';

const initialState = {
    loading: false,
    failed: false,
    data: null,
};


export default function (state = initialState, action) {
    switch (action.type) {
        case REQUESTING_DATA:
            return Object.assign({}, state, {data: null, loading: true, failed: false });
        case LOAD_DATA:
            return Object.assign({}, state, {data: action.payload, loading: false, failed: false });
        case LOAD_DATA_FAILED:
            return Object.assign({}, state, {data: null, loading: false, failed: true });
        default:
            return state;
    }
}