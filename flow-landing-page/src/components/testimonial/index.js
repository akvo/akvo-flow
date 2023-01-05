import React from "react";
import "./index.scss";
import oneDrop from "../../images/organisations/one-drop-logo.png";
import { Navigation, Pagination, A11y } from "swiper";

import { Swiper, SwiperSlide } from "swiper/react";

// Import Swiper styles
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";

const Testimonial = () => {
  const testimonials = [
    {
      name: "Anna Zisa",
      description:
        "Akvo Flow has allowed the Lazos de Agua Program supported by the One Drop Foundation to monitor water, sanitation and hygiene service levels for households and WASH behaviours. Akvo Flow supported collecting data through over 5,000 household surveys in 5 countries in Latin America. At a program level, using a harmonized data collection tool has facilitated data analysis saving plenty of time and effort. Implementing partners of the program have even found other beneficial uses for the app than those originally planned. Plus, the technical support from Akvo has been timely any time it was needed.",
      role: "Manager - Monitoring, evaluation, research and learning, International Programs at One Drop Foundation",
      organisation: { name: "One Drop", logo: oneDrop },
    },
    {
      name: "Anna Zisa",
      description:
        "Akvo Flow has allowed the Lazos de Agua Program supported by the One Drop Foundation to monitor water, sanitation and hygiene service levels for households and WASH behaviours. Akvo Flow supported collecting data through over 5,000 household surveys in 5 countries in Latin America. At a program level, using a harmonized data collection tool has facilitated data analysis saving plenty of time and effort. Implementing partners of the program have even found other beneficial uses for the app than those originally planned. Plus, the technical support from Akvo has been timely any time it was needed.",
      role: "Manager - Monitoring, evaluation, research and learning, International Programs at One Drop Foundation",
      organisation: { name: "One Drop", logo: oneDrop },
    },
    {
      name: "Anna Zisa",
      description:
        "Akvo Flow has allowed the Lazos de Agua Program supported by the One Drop Foundation to monitor water, sanitation and hygiene service levels for households and WASH behaviours. Akvo Flow supported collecting data through over 5,000 household surveys in 5 countries in Latin America. At a program level, using a harmonized data collection tool has facilitated data analysis saving plenty of time and effort. Implementing partners of the program have even found other beneficial uses for the app than those originally planned. Plus, the technical support from Akvo has been timely any time it was needed.",
      role: "Manager - Monitoring, evaluation, research and learning, International Programs at One Drop Foundation",
      organisation: { name: "One Drop", logo: oneDrop },
    },
    {
      name: "Anna Zisa",
      description:
        "Akvo Flow has allowed the Lazos de Agua Program supported by the One Drop Foundation to monitor water, sanitation and hygiene service levels for households and WASH behaviours. Akvo Flow supported collecting data through over 5,000 household surveys in 5 countries in Latin America. At a program level, using a harmonized data collection tool has facilitated data analysis saving plenty of time and effort. Implementing partners of the program have even found other beneficial uses for the app than those originally planned. Plus, the technical support from Akvo has been timely any time it was needed.",
      role: "Manager - Monitoring, evaluation, research and learning, International Programs at One Drop Foundation",
      organisation: { name: "One Drop", logo: oneDrop },
    },
  ];
  return (
    <div className="testimonial">
      <h3 className="heading">
        What our <span>partners</span> say
      </h3>
      <Swiper
        modules={[Navigation, A11y]}
        spaceBetween={50}
        slidesPerView={1}
        navigation
        pagination={{ clickable: true }}
      >
        {testimonials.map((testimonial, index) => {
          return (
            <SwiperSlide key={index}>
              <div>
                <div className="wrapper">
                  <blockquote className="testimonial-quote">
                    <q>{testimonial.description}</q>
                  </blockquote>
                  <div className="container">
                    <b className="testimonial-name">{testimonial.name}</b>
                    <p className="testimonial-role">{testimonial.role}</p>
                  </div>
                  <img
                    className="testimonial-organisation-logo"
                    src={testimonial.organisation.logo}
                    alt={testimonial.organisation.name}
                  />
                </div>
              </div>
            </SwiperSlide>
          );
        })}
      </Swiper>
    </div>
  );
};

export default Testimonial;
