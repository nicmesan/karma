import React, { Component } from 'react';
import FacebookChart from '../components/chart';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import Input from '../components/input';
import { requestData, loadData } from '../actions/facebook';

class App extends Component {

	getData (pageId) {
		this.props.requestData();
		this.props.loadData(pageId);
	}

	render() {
        if (this.props.facebook.loading) {
        	return <div>Loading...</div>
		}
		else if (this.props.facebook.data) {
			return (
				<div>
					<h1>Your page insights :)</h1>
					<FacebookChart data={this.props.facebook.data} />
				</div>
			)
		}

		return (
			<div className="container">
				<h1>Please Enter your page ID</h1>
				<Input getData={this.getData.bind(this)} />
			</div>
        );
	}
}

function mapStateToProps (state) {
	return ({ facebook: state.facebook });
}
function mapDispatchToProps (dispatch) {
    return bindActionCreators({ requestData, loadData }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(App);