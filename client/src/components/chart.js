import React, { Component } from 'react';
import Chart, { Line } from 'react-chartjs-2';


Chart.defaults = {
    defaultFontFamily: "Raleway",
    defaultFontSize: "20px",
};

export default class FacebookChart extends Component {
    constructor (props) {
        super(props);

        this.data = {
            labels: ["0hs", "1hs", "2hs", "3hs", "4hs", "5hs", "6hs", "7hs", "8hs", "9hs", "10hs", "11hs", "12hs", "13hs", "14hs", "15hs", "16hs", "17hs", "18hs", "19hs", "20hs", "21hs", "22hs", "23hs" ],
            datasets: [
                {
                    label: "Shares",
                    backgroundColor: "rgb(229,238,193)",
                    strokeColor: "rgba(151,187,205,0.8)",
                    highlightFill: "rgba(151,187,205,0.75)",
                    highlightStroke: "rgba(151,187,205,1)",
                    scaleGridLineColor : "rgba(151,187,205,1)",
                    scaleShowHorizontalLines: true,
                    scaleShowVerticalLines: true,
                    data: this.props.data,
                    spanGaps: false,
                },
            ]
        };
    }

    render() {
        return (
            <div>
                <Line data={this.data} />
            </div>
        )
    }
}
