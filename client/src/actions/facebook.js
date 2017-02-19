import { REQUESTING_DATA, LOAD_DATA } from '../constants';
import axios from 'axios';

export function requestData () {
    return {
        type: REQUESTING_DATA,

    }
}

export function loadData () {
    return {
        type: LOAD_DATA,
        payload: [23, 32,12,32,34,13,66,43,1,44,23,24,23,123,32,4,34,23, 23,33,65,54,71,75]
    }
}
